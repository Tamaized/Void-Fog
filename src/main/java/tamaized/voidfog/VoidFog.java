package tamaized.voidfog;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Mod("voidfog")
public class VoidFog {

	static boolean active;
	static final float[] colors = new float[3];
	static float color = 0F;
	static float fog = 1F;

	static class Config {

		static Config INSTANCE;
		ForgeConfigSpec.IntValue y;
		ForgeConfigSpec.DoubleValue distance;
		ForgeConfigSpec.BooleanValue voidscape;

		ForgeConfigSpec.ConfigValue<List<? extends String>> blacklistedDims;
		ForgeConfigSpec.BooleanValue whitelistToggle;

		public Config(ForgeConfigSpec.Builder builder) {
			y = builder.
					translation("voidfog.config.y").
					comment("The Y value in which void fog takes effect. (Min World Height + Y Value)").
					defineInRange("y", 12, 0, Integer.MAX_VALUE);
			distance = builder.
					translation("voidfog.config.distance").
					comment("Defines how far away the fog should be from your player. Higher numbers mean further away.").
					defineInRange("distance", 30, 0, Double.MAX_VALUE);
			voidscape = builder.
					translation("voidfog.config.voidscape").
					comment("Enable the effect everywhere in the mod Voidscape's main Dimension.").
					define("voidscape", true);
			blacklistedDims = builder.
					translation("voidfog.config.blacklisted_dims").
					comment("Defines which dimensions shouldnt render any void fog. Each entry should be a valid dimension id. For example, to blacklist void fog from appearing in Twilight Forest, add \"twilightforest:twilight_forest\" to this list.").
					defineList("dimension_blacklist", new ArrayList<>(), s -> s instanceof String string && ResourceLocation.isValidResourceLocation(string));
			whitelistToggle = builder.
					translation("voidfog.config.whitelist_toggle").
					comment("Defines whether the dimension blacklist should function as a whitelist instead, meaning that only dimensions in that config option will render void fog.").
					define("toggle_whitelist", false);
		}
	}

	public VoidFog() {
		IEventBus busForge = MinecraftForge.EVENT_BUS;
		final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, specPair.getRight());
		Config.INSTANCE = specPair.getLeft();
		busForge.addListener((Consumer<ViewportEvent.RenderFog>) event -> {
			if (active || fog < 1F) {
				float f = Config.INSTANCE.distance.get().floatValue();
				f = f >= event.getFarPlaneDistance() ? event.getFarPlaneDistance() : Mth.clampedLerp(f, event.getFarPlaneDistance(), fog);
				float shift = (float) ((active ? (fog > 0.5F ? 0.005F : 0.001F) : (fog > 0.25F ? 0.01F : 0.001F)) * event.getPartialTick());
				if (active)
					fog -= shift;
				else
					fog += shift;
				fog = Mth.clamp(fog, 0F, 1F);

				RenderSystem.setShaderFogStart(0.0F);
				RenderSystem.setShaderFogEnd(f);
			}
		});
		busForge.addListener((Consumer<ViewportEvent.ComputeFogColor>) event -> {
			if (active || color > 0F) {
				final float[] realColors = {event.getRed(), event.getGreen(), event.getBlue()};
				for (int i = 0; i < 3; i++) {
					final float real = realColors[i];
					final float c = 0;
					colors[i] = real == c ? c : Mth.clampedLerp(real, c, color);
				}
				if (active)
					color += (float) (0.1F * event.getPartialTick());
				else
					color -= (float) (0.005F * event.getPartialTick());
				color = Mth.clamp(color, 0F, 1F);
				event.setRed(colors[0]);
				event.setGreen(colors[1]);
				event.setBlue(colors[2]);
			}
		});
		busForge.addListener((Consumer<TickEvent.PlayerTickEvent>) event -> {
			if (event.player != Minecraft.getInstance().player)
				return;

			if ((event.player.level() != null && (event.player.getY() <= event.player.level().getMinBuildHeight() + Config.INSTANCE.y.get() && Config.INSTANCE.whitelistToggle.get() == Config.INSTANCE.blacklistedDims.get().contains(event.player.level().dimension().location().toString())) || checkForVoidscapeDimension(event.player.level()))) {
				active = !event.player.hasEffect(MobEffects.NIGHT_VISION);
				RandomSource random = event.player.getRandom();
				for (int l = 0; l < 100; ++l) {
					int i1 = event.player.blockPosition().getX() + random.nextInt(16) - random.nextInt(16);
					int j1 =  event.player.blockPosition().getY() + random.nextInt(16) - random.nextInt(16);
					int k1 =  event.player.blockPosition().getZ() + random.nextInt(16) - random.nextInt(16);
					BlockState block = event.player.level().getBlockState(new BlockPos(i1, j1, k1));

					if (block.isAir() && random.nextInt(Config.INSTANCE.y.get()) > j1) {
						event.player.level().addParticle(ParticleTypes.ASH, i1 + random.nextFloat(), j1 + random.nextFloat(), k1 + random.nextFloat(), 0.0D, 0.0D, 0.0D);
					}
				}
			} else
				active = false;
		});
	}

	public static final ResourceKey<Level> WORLD_KEY_VOID = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("voidscape", "void"));

	public static boolean checkForVoidscapeDimension(Level world) {
		return Config.INSTANCE.voidscape.get() && world.dimension().location().equals(WORLD_KEY_VOID.location());
	}

}
