package justfatlard.spawner_shards;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/**
 * A dragon spawner fires once.
 *
 * <p>Every other head makes a spawner in the ordinary sense: a thing that keeps producing. A
 * dragon does not survive that reading, so the cage goes into the dragon and there is nothing
 * left to spawn a second one. What you get for a dragon head is one dragon, at a time and place
 * of your choosing, which is a different thing from a dragon farm and the only version of this
 * worth having.
 */
public final class DragonSpawner {
	/** Creeper strength. Enough to say what happened, and to be standing too close. */
	private static final float BLAST_POWER = 4.0F;

	private DragonSpawner() {}

	/** Called for every spawner spawn; does nothing unless what came out was a dragon. */
	public static void consumeIfDragon(ServerLevel world, BlockPos pos, Entity spawned) {
		if (spawned.getType() != EntityTypes.ENDER_DRAGON) return;
		if (!world.getBlockState(pos).is(Blocks.SPAWNER)) return;

		// Before the blast, while there is still somebody standing near enough to be told.
		Awards.dragonSpent(world, pos);

		// Gone before the blast, so the spawner cannot come back as shards: the point is that it
		// was spent, and an explosion that drops its own materials has not spent anything.
		world.removeBlock(pos, false);

		// Credited to the dragon, and MOB rather than BLOCK, so a server that has turned off
		// mobGriefing still gets the bang without losing the wall behind it. The spawner is
		// consumed either way; only the collateral is the game rule's to decide.
		world.explode(spawned, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
			BLAST_POWER, Level.ExplosionInteraction.MOB);
	}
}
