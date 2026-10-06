package justfatlard.spawner_shards.gametest;

import justfatlard.pandorical.gametest.Pictures;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerConnection;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

/**
 * The pictures for the readme and the mod page: nine spawner shards filling a crafting table, and the spawner they make.
 */
public final class Showcase implements FabricClientGameTest {
	private static final long SEED = 20261005L;

	@Override
	public void runTest(ClientGameTestContext context) {
		try (TestSingleplayerContext world = Pictures.world(context, SEED)) {
			TestServerContext server = world.getServer();
			TestServerConnection connection = world.getConnection();
			Pictures.stage(context, server, Pictures.MORNING);

			BlockPos at = server.computeOnServer(s -> {
				BlockPos spawn = connection.getServerPlayer().blockPosition();
				return Pictures.dryGround(s.overworld(), spawn.getX(), spawn.getZ(), 16);
			});
			if (at == null) throw new AssertionError("no dry ground near spawn");
			server.runOnServer(s -> {
				ServerLevel level = s.overworld();
				level.setBlockAndUpdate(at, Blocks.CRAFTING_TABLE.defaultBlockState());
			});
			Pictures.carry(server, connection,
				new ItemStack(Items.IRON_PICKAXE), new ItemStack(Items.IRON_AXE), new ItemStack(Items.TORCH, 48),
				new ItemStack(Items.BREAD, 12), new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.COBBLESTONE, 64),
				new ItemStack(Items.OAK_PLANKS, 30), new ItemStack(Items.CRAFTING_TABLE), new ItemStack(Items.STONE_SWORD));
			Pictures.open(context, server, connection, at);
			server.runOnServer(s -> {
				var menu = connection.getServerPlayer().containerMenu;
				for (int i = 1; i <= 9; i++) menu.getSlot(i).set(new ItemStack(justfatlard.spawner_shards.Main.SPAWNER_SHARDS));
				menu.broadcastChanges();
			});
			context.waitTicks(10);
			Pictures.shootScreen(context, connection, "shards-to-spawner");
		}
	}
}
