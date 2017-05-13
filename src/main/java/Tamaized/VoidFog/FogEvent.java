package Tamaized.VoidFog;

import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class FogEvent {

	@SubscribeEvent
	public void particles(ClientTickEvent e) {
		if(Minecraft.getMinecraft().isGamePaused()) return;
		EntityPlayer player = Minecraft.getMinecraft().player;
		World world = Minecraft.getMinecraft().world;
		if (player != null && world != null) {
			int x = MathHelper.floor(player.posX);
			int y = MathHelper.floor(player.posY);
			int z = MathHelper.floor(player.posZ);
			byte b0 = 16;
			Random random = new Random();

			for (int l = 0; l < 1000; ++l) {
				int i1 = x + world.rand.nextInt(b0) - world.rand.nextInt(b0);
				int j1 = y + world.rand.nextInt(b0) - world.rand.nextInt(b0);
				int k1 = z + world.rand.nextInt(b0) - world.rand.nextInt(b0);
				IBlockState block = world.getBlockState(new BlockPos(i1, j1, k1));

				if (block.getMaterial() == Material.AIR) {
					if ((world.rand.nextInt(8) > (VoidFog.voidcraft && world.provider.getDimension() == Tamaized.Voidcraft.VoidCraft.config.getDimensionIdVoid() ? 0 : j1)) && ((world.getWorldInfo().getTerrainType() != WorldType.FLAT && !world.provider.hasNoSky()) || (VoidFog.voidcraft && world.provider.getDimension() == Tamaized.Voidcraft.VoidCraft.config.getDimensionIdVoid()))) {
						world.spawnParticle(EnumParticleTypes.SUSPENDED_DEPTH, (double) ((float) i1 + world.rand.nextFloat()), (double) ((float) j1 + world.rand.nextFloat()), (double) ((float) k1 + world.rand.nextFloat()), 0.0D, 0.0D, 0.0D);
					}
				} else {
					block.getBlock().randomDisplayTick(block, world, new BlockPos(i1, j1, k1), random);
				}
			}
		}
	}

	@SubscribeEvent
	public void render(RenderFogEvent e) {
		Entity entity = e.getEntity();
		WorldClient worldclient = Minecraft.getMinecraft().world;
		boolean flag = false;
		if (entity instanceof EntityPlayer) {
			flag = ((EntityPlayer) entity).capabilities.isCreativeMode;
		}
		float f1 = e.getFarPlaneDistance();
		if ((worldclient.getWorldInfo().getTerrainType() != WorldType.FLAT && !worldclient.provider.hasNoSky() && !flag ) || (VoidFog.voidcraft && worldclient.provider.getDimension() == Tamaized.Voidcraft.VoidCraft.config.getDimensionIdVoid())) {
			double d0 = (double) ((entity.getBrightnessForRender((float) e.getRenderPartialTicks()) & 15728640) >> 20) / 16.0D + ((VoidFog.voidcraft && worldclient.provider.getDimension() == Tamaized.Voidcraft.VoidCraft.config.getDimensionIdVoid()) ? 15 : (entity.lastTickPosY + (entity.posY - entity.lastTickPosY)) * (double) e.getRenderPartialTicks() + 4.0D) / 32.0D;

			if (d0 < 1.0D) {
				if (d0 < 0.0D) {
					d0 = 0.0D;
				}

				d0 *= d0;
				float f2 = 100.0F * (float) d0;

				if (f2 < 5.0F) {
					f2 = 5.0F;
				}

				if (f1 > f2) {
					f1 = f2;
				}
			}
		}

		GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);

		if (e.getFogMode() < 0) {
			GL11.glFogf(GL11.GL_FOG_START, 0.0F);
			GL11.glFogf(GL11.GL_FOG_END, f1);
		} else {
			GL11.glFogf(GL11.GL_FOG_START, f1 * 0.75F);
			GL11.glFogf(GL11.GL_FOG_END, f1);
		}

		if (GLContext.getCapabilities().GL_NV_fog_distance) {
			GL11.glFogi(34138, 34139);
		}

		if (worldclient.provider.doesXZShowFog((int) entity.posX, (int) entity.posZ)) {
			GL11.glFogf(GL11.GL_FOG_START, f1 * 0.05F);
			GL11.glFogf(GL11.GL_FOG_END, Math.min(f1, 192.0F) * 0.5F);
		}
	}

	@SubscribeEvent
	public void color(FogColors e) {
		Entity entity = e.getEntity();
		WorldClient worldclient = Minecraft.getMinecraft().world;
		double d0 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * e.getRenderPartialTicks() * worldclient.provider.getVoidFogYFactor();

		if (d0 < 1.0D) {
			if (d0 < 0.0D) {
				d0 = 0.0D;
			}

			d0 *= d0;
			if((VoidFog.voidcraft && worldclient.provider.getDimension() == Tamaized.Voidcraft.VoidCraft.config.getDimensionIdVoid())) d0 = 0;
			e.setRed((float) ((double) e.getRed() * d0));
			e.setGreen((float) ((double) e.getGreen() * d0));
			e.setBlue((float) ((double) e.getBlue() * d0));
		}
	}

}
