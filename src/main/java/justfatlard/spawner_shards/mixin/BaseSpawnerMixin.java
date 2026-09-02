package justfatlard.spawner_shards.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import justfatlard.spawner_shards.DragonSpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;

/**
 * The one call in a spawner's tick that means a spawn actually landed.
 *
 * <p>Wrapping it rather than watching for dragons appearing in the world is what makes the rule
 * exact: this fires for that spawner, at that position, with the entity it just produced, so a
 * dragon spawner cannot get away with a second dragon by spawning it a few blocks further out
 * than a proximity search would look.
 */
@Mixin(BaseSpawner.class)
public class BaseSpawnerMixin {
	@WrapOperation(
		method = "serverTick",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/server/level/ServerLevel;tryAddFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)Z"))
	private boolean spawnerShards$spendOnDragon(ServerLevel world, Entity spawned, Operation<Boolean> original,
			@Local(argsOnly = true) BlockPos pos) {
		boolean spawnedIn = original.call(world, spawned);

		if (spawnedIn) DragonSpawner.consumeIfDragon(world, pos, spawned);

		return spawnedIn;
	}
}
