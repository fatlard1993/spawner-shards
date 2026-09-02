package justfatlard.spawner_shards;

import java.util.List;
import java.util.Optional;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.advancements.predicates.DataComponentMatchers;
import net.minecraft.advancements.predicates.EnchantmentPredicate;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

/**
 * Gives the spawner something to drop.
 *
 * <p>Vanilla ships {@code blocks/spawner} as a real table that simply has no pools in it, which
 * is what makes this an append rather than an override: the two pools below join the table
 * vanilla already consults when the block breaks, and anything another mod put there stays.
 *
 * <p>Nine shards build a spawner, and the two enchantments that help are the two that cannot
 * share a pickaxe, so they buy different things rather than more of the same. Silk touch raises
 * the floor: never one shard, and a steady three on average. Fortune raises the ceiling and
 * leaves the floor where it was, so a fortune III pickaxe averages the same three but can still
 * roll a single shard, and can also roll five. Careful or lucky, take your pick.
 */
public final class SpawnerShardLoot {
	private static final int PLAIN_MIN = 1, PLAIN_MAX = 2;
	private static final int SILK_MIN = 2, SILK_MAX = 4;

	/** Read off the block rather than written out, so a renamed vanilla table cannot silently miss. */
	private static final Optional<ResourceKey<LootTable>> SPAWNER_TABLE = Blocks.SPAWNER.getLootTable();

	private SpawnerShardLoot() {}

	public static void register() {
		LootTableEvents.MODIFY.register((key, builder, source, registries) -> {
			// Only vanilla's own copy. A datapack that has deliberately rewritten the spawner's
			// table has said what it wants in it, and appending to that is overruling an author
			// who was more specific than this mod is.
			if (source != LootTableSource.VANILLA) return;
			if (SPAWNER_TABLE.isEmpty() || !key.equals(SPAWNER_TABLE.get())) return;

			builder.pool(silkTouched(registries).build());
			builder.pool(mined(registries).build());
		});
	}

	private static LootPool.Builder silkTouched(HolderLookup.Provider registries) {
		return shards(SILK_MIN, SILK_MAX)
			.when(hasSilkTouch(registries))
			.when(ExplosionCondition.survivesExplosion());
	}

	/**
	 * Everything that is not silk touch, which is also everywhere fortune can reach: the two
	 * are exclusive on a tool, so the bonus belongs on this pool alone, the way every vanilla
	 * ore table places it.
	 */
	private static LootPool.Builder mined(HolderLookup.Provider registries) {
		return shards(PLAIN_MIN, PLAIN_MAX)
			.apply(ApplyBonusCount.addUniformBonusCount(enchantment(registries, Enchantments.FORTUNE)))
			.when(hasSilkTouch(registries).invert())
			.when(ExplosionCondition.survivesExplosion());
	}

	private static LootPool.Builder shards(int min, int max) {
		return LootPool.lootPool()
			.setRolls(ConstantValue.exactly(1))
			.add(LootItem.lootTableItem(Main.SPAWNER_SHARDS))
			.apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)));
	}

	private static LootItemCondition.Builder hasSilkTouch(HolderLookup.Provider registries) {
		return MatchTool.toolMatches(ItemPredicate.Builder.item()
			.withComponents(DataComponentMatchers.Builder.components()
				.partial(DataComponentPredicates.ENCHANTMENTS, EnchantmentsPredicate.enchantments(List.of(
					new EnchantmentPredicate(
						enchantment(registries, Enchantments.SILK_TOUCH),
						MinMaxBounds.Ints.atLeast(1)))))
				.build()));
	}

	private static Holder<Enchantment> enchantment(HolderLookup.Provider registries, ResourceKey<Enchantment> key) {
		return registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
	}
}
