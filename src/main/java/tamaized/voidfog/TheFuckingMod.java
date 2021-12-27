package tamaized.voidfog;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Random;
import java.util.function.Consumer;

@Mod("voidfog")
public class TheFuckingMod {

	static boolean active;
	static final float[] colors = new float[3];
	static float color = 0F;
	static float fog = 1F;

	static class Config {

		static Config INSTANCE;
		ForgeConfigSpec.IntValue y;

		public Config(ForgeConfigSpec.Builder builder) {
			y = builder.
					translation("voidfog.config.y").
					comment("The Y value in which void fog takes effect. (Min World Height + Y Value)").
					defineInRange("y", 12, 0, Integer.MAX_VALUE);
		}

	}

	public TheFuckingMod() {
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, ()->new IExtensionPoint.
				DisplayTest(()-> FMLNetworkConstants.IGNORESERVERONLY, (remote, isServer)-> true));
		IEventBus busForge = MinecraftForge.EVENT_BUS;
		final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, specPair.getRight());
		Config.INSTANCE = specPair.getLeft();
		busForge.addListener((Consumer<EntityViewRenderEvent.RenderFogEvent>) event -> {
			if (active || fog < 1F) {
				float f = 3F;
				f = f >= event.getFarPlaneDistance() ? event.getFarPlaneDistance() : Mth.clampedLerp(f, event.getFarPlaneDistance(), fog);
				float shift = (float) ((active ? (fog > 0.25F ? 0.1F : 0.0005F) : (fog > 0.25F ? 0.001F : 0.0001F)) * event.getRenderPartialTicks());
				if (active)
					fog -= shift;
				else
					fog += shift;
				fog = Mth.clamp(fog, 0F, 1F);

				if (event.getType() == FogRenderer.FogMode.FOG_SKY) {
					RenderSystem.setShaderFogStart(0.0F);
					RenderSystem.setShaderFogEnd(f);
				} else {
					RenderSystem.setShaderFogStart(f * 0.75F);
					RenderSystem.setShaderFogEnd(f);
				}
			}
		});
		busForge.addListener((Consumer<EntityViewRenderEvent.FogColors>) event -> {
			if (active || color > 0F) {
				final float[] realColors = {event.getRed(), event.getGreen(), event.getBlue()};
				for (int i = 0; i < 3; i++) {
					final float real = realColors[i];
					final float c = 0;
					colors[i] = real == c ? c : Mth.clampedLerp(real, c, color);
				}
				float shift = (float) (0.1F * event.getRenderPartialTicks());
				if (active)
					color += shift;
				else
					color -= shift;
				color = Mth.clamp(color, 0F, 1F);
				event.setRed(colors[0]);
				event.setGreen(colors[1]);
				event.setBlue(colors[2]);
			}
		});
		busForge.addListener((Consumer<TickEvent.PlayerTickEvent>) event -> {
			if(event.player.level != null && event.player.getY() <= event.player.level.getMinBuildHeight() + Config.INSTANCE.y.get()) {
				active = true;
				Random random = event.player.getRandom();
				for (int i = 0; i < 15; i++) {
					Vec3 vec = event.player.position().add(0, random.nextDouble() * 3D, 0).
							add(new Vec3(random.nextDouble() * 6D, 0D, 0D).yRot((float) Math.toRadians(random.nextInt(360))));
					event.player.level.addParticle(ParticleTypes.ASH, vec.x, vec.y, vec.z, 0, 0, 0);
				}
			} else
				active = false;
		});
	}

}
