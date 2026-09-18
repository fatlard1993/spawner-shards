package justfatlard.spawner_shards;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * The advancements this mod hands out.
 *
 * <p>Given to whoever is nearest the cage as it fires, and that is not a guess here the way it
 * would be elsewhere: a spawner only runs with somebody inside its activation range, so there is
 * always a player close by, and the one closest to a dragon spawner going off is the one who built
 * it and then stood there to watch.
 */
public final class Awards {
	private Awards() {}

	/** A spawner's activation range, which is who could have set this off. */
	private static final double IN_RANGE = 16.0;

	public static void dragonSpent(ServerLevel world, BlockPos pos) {
		Player nearest = world.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
			IN_RANGE, false);
		if (nearest instanceof ServerPlayer player) award(player, "one_dragon");
	}

	private static void award(ServerPlayer player, String path) {
		if (player.level().getServer() == null) return;
		AdvancementHolder holder = player.level().getServer().getAdvancements()
			.get(Identifier.fromNamespaceAndPath(Main.MOD_ID, path));
		if (holder == null) return;

		AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
		if (progress.isDone()) return;
		for (String criterion : progress.getRemainingCriteria()) {
			player.getAdvancements().award(holder, criterion);
		}
	}
}
