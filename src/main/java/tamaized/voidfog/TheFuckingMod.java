package tamaized.voidfog;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.network.FMLNetworkConstants;
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
		ForgeConfigSpec.BooleanValue voidscape;

		public Config(ForgeConfigSpec.Builder builder) {
			y = builder.
					translation("voidfog.config.y").
					comment("The Y value in which void fog takes effect. (Min World Height + Y Value)").
					defineInRange("y", 12, 0, Integer.MAX_VALUE);
			voidscape = builder.
					translation("voidfog.config.voidscape").
					comment("Enable the effect everywhere in the mod Voidscape's main Dimension.").
					define("voidscape", true);
		}

	}

	public TheFuckingMod() {
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, ()->Pair.of(()-> FMLNetworkConstants.IGNORESERVERONLY, (remote, isServer)-> true));
		IEventBus busForge = MinecraftForge.EVENT_BUS;
		final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, specPair.getRight());
		Config.INSTANCE = specPair.getLeft();
		busForge.addListener((Consumer<EntityViewRenderEvent.RenderFogEvent>) event -> {
			if (active || fog < 1F) {
				float f = 3F;
				f = f >= event.getFarPlaneDistance() ? event.getFarPlaneDistance() : (float) MathHelper.clampedLerp(f, event.getFarPlaneDistance(), fog);
				float shift = (float) ((active ? (fog > 0.25F ? 0.1F : 0.0005F) : (fog > 0.25F ? 0.001F : 0.0001F)) * event.getRenderPartialTicks());
				if (active)
					fog -= shift;
				else
					fog += shift;
				fog = MathHelper.clamp(fog, 0F, 1F);

				if (event.getType() == FogRenderer.FogType.FOG_SKY) {
					RenderSystem.fogStart(0.0F);
					RenderSystem.fogEnd(f);
				} else {
					RenderSystem.fogStart(f * 0.75F);
					RenderSystem.fogEnd(f);
				}
			}
		});
		busForge.addListener((Consumer<EntityViewRenderEvent.FogColors>) event -> {
			if (active || color > 0F) {
				final float[] realColors = {event.getRed(), event.getGreen(), event.getBlue()};
				for (int i = 0; i < 3; i++) {
					final float real = realColors[i];
					final float c = 0;
					colors[i] = real == c ? c : (float) MathHelper.clampedLerp(real, c, color);
				}
				float shift = (float) (0.1F * event.getRenderPartialTicks());
				if (active)
					color += shift;
				else
					color -= shift;
				color = MathHelper.clamp(color, 0F, 1F);
				event.setRed(colors[0]);
				event.setGreen(colors[1]);
				event.setBlue(colors[2]);
			}
		});
		busForge.addListener((Consumer<TickEvent.PlayerTickEvent>) event -> {
			if (event.player != Minecraft.getInstance().player)
				return;
			if (event.player.level != null && (event.player.getY() <= Config.INSTANCE.y.get() || checkForVoidscapeDimension(event.player.level))) {
				active = true;
				Random random = event.player.getRandom();
				for (int i = 0; i < 15; i++) {
					Vector3d vec = event.player.position().add(0, random.nextDouble() * 3D, 0).
							add(new Vector3d(random.nextDouble() * 6D, 0D, 0D).yRot((float) Math.toRadians(random.nextInt(360))));
					event.player.level.addParticle(ParticleTypes.ASH, vec.x, vec.y, vec.z, 0, 0, 0);
				}
			} else
				active = false;
		});
	}

	public static final RegistryKey<World> WORLD_KEY_VOID = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("voidscape", "void"));

	public static boolean checkForVoidscapeDimension(World world) {
		return Config.INSTANCE.voidscape.get() && world.dimension().location().equals(WORLD_KEY_VOID.location());
	}

}
