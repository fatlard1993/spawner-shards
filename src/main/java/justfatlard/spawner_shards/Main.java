package justfatlard.spawner_shards;

import justfatlard.pandorical.api.ItemRegistration;
import justfatlard.pandorical.api.PandoricalApi;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class Main implements ModInitializer {
	public static final String MOD_ID = "spawner-shards-justfatlard";

	public static final Identifier SPAWNER_SHARDS_ID = Identifier.fromNamespaceAndPath(MOD_ID, "spawner_shards");

	public static final ResourceKey<Item> SPAWNER_SHARDS_KEY = ResourceKey.create(Registries.ITEM, SPAWNER_SHARDS_ID);

	public static final Item SPAWNER_SHARDS = new Item(
		new Item.Properties().setId(SPAWNER_SHARDS_KEY)
	);

	@Override
	public void onInitialize() {
		if (PandoricalApi.isAvailable()) {
			PandoricalApi.content().registerItem(MOD_ID + ":spawner_shards", new ItemRegistration()
				.model(MOD_ID + ":item/spawner_shards"));
			PandoricalApi.content().registerModAssets(MOD_ID);
			MobHeadSpawner.claimSpawnerInteraction();
		}

		Registry.register(BuiltInRegistries.ITEM, SPAWNER_SHARDS_ID, SPAWNER_SHARDS);

		ResourceKey<CreativeModeTab> tabKey = ResourceKey.create(
			Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(MOD_ID, "spawner_shards"));
		CreativeModeTab spawnerShardsGroup = FabricCreativeModeTab.builder()
			.title(Component.literal("Spawner Shards"))
			.icon(() -> new ItemStack(SPAWNER_SHARDS))
			.displayItems((context, entries) -> {
				entries.accept(new ItemStack(SPAWNER_SHARDS));
			})
			.build();
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, tabKey, spawnerShardsGroup);

		SpawnerShardLoot.register();
		MobHeadSpawner.register();

		System.out.println("[" + MOD_ID + "] Loaded (server-side with Pandorical)");
	}
}
