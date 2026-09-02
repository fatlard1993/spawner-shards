package justfatlard.spawner_shards;

import java.util.Map;

import justfatlard.pandorical.api.BlockRegistration;
import justfatlard.pandorical.api.PandoricalApi;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A mob head sets a spawner, the way a spawn egg does.
 *
 * <p>This walks the same path as {@code SpawnEggItem}: same {@link Spawner} target, so it works
 * on anything vanilla's egg works on; same feature gate, so a head cannot set a spawner on a
 * world where an egg cannot; same block update, game event and stack cost afterwards. The only
 * difference is what is in your hand.
 *
 * <p>The player head is the one skull left out, because it names a person rather than a mob and
 * there is no entity behind it to spawn.
 */
public final class MobHeadSpawner {
	private static final Map<Item, EntityType<?>> HEADS = Map.of(
		Items.SKELETON_SKULL, EntityTypes.SKELETON,
		Items.WITHER_SKELETON_SKULL, EntityTypes.WITHER_SKELETON,
		Items.ZOMBIE_HEAD, EntityTypes.ZOMBIE,
		Items.CREEPER_HEAD, EntityTypes.CREEPER,
		Items.PIGLIN_HEAD, EntityTypes.PIGLIN,
		Items.DRAGON_HEAD, EntityTypes.ENDER_DRAGON);

	private MobHeadSpawner() {}

	public static void register() {
		UseBlockCallback.EVENT.register(MobHeadSpawner::onUseBlock);
	}

	/**
	 * Tells a Pandorical client that a right-click on a spawner may be the server's to answer.
	 *
	 * <p>Without it the client does what it does with any block it has no special handling for:
	 * predicts that a right-click places whatever is in hand. The head appears on top of the
	 * spawner, vanishes a tick later, and leaves the stack count wrong until something forces a
	 * resync.
	 *
	 * <p>A vanilla block rather than one of ours, which the content sync would normally skip. The
	 * claim is made once at startup and cannot be narrowed to only the ticks a head is held, so
	 * the cost is that every right-click on a spawner is placed against a tick later than it was.
	 */
	public static void claimSpawnerInteraction() {
		PandoricalApi.content().registerBlock("minecraft:spawner", new BlockRegistration().interactive());
	}

	private static InteractionResult onUseBlock(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
		if (!(world instanceof ServerLevel serverWorld)) return InteractionResult.PASS;

		// A spawn egg has no second meaning, but a head does: it is also a block you put on
		// things. Sneaking is where vanilla already says "use the item, leave the block alone",
		// so it is the way to hang a skull on a spawner instead of feeding it one.
		if (player.isSecondaryUseActive()) return InteractionResult.PASS;

		ItemStack stack = player.getItemInHand(hand);
		EntityType<?> type = HEADS.get(stack.getItem());
		if (type == null) return InteractionResult.PASS;

		BlockPos pos = hitResult.getBlockPos();
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (!(blockEntity instanceof Spawner spawner)) return InteractionResult.PASS;

		if (!serverWorld.isSpawnerBlockEnabled()) {
			player.sendSystemMessage(Component.literal("Spawner blocks are disabled in this world."));
			return InteractionResult.FAIL;
		}

		spawner.setEntityId(type, world.getRandom());

		BlockState state = world.getBlockState(pos);
		world.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
		world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
		stack.consume(1, player);

		return InteractionResult.SUCCESS;
	}
}
