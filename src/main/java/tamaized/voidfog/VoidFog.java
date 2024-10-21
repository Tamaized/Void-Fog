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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import tamaized.beanification.Autowired;
import tamaized.beanification.BeanContext;

import java.util.function.Consumer;

@Mod(VoidFog.MODID)
public class VoidFog {

	public static final String MODID = "voidfog";

	static {
		BeanContext.init();
	}

	static boolean active;
	static final float[] colors = new float[3];
	static float color = 0F;
	static float fog = 1F;

	@Autowired
	private Config config;

	public VoidFog() {
		BeanContext.enableMainModClassInjections(this);
		IEventBus busForge = NeoForge.EVENT_BUS;
		busForge.addListener((Consumer<ViewportEvent.RenderFog>) event -> {
			if (active || fog < 1F) {
				float f = config.distance.get().floatValue();
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
		busForge.addListener((Consumer<PlayerTickEvent.Pre>) event -> {
			if (event.getEntity() != Minecraft.getInstance().player)
				return;

			if ((event.getEntity().level() != null && (event.getEntity().getY() <= event.getEntity().level().getMinBuildHeight() + config.y.get() && config.whitelistToggle.get() == config.blacklistedDims.get().contains(event.getEntity().level().dimension().location().toString())) || checkForVoidscapeDimension(event.getEntity().level()))) {
				active = !event.getEntity().hasEffect(MobEffects.NIGHT_VISION);
				RandomSource random = event.getEntity().getRandom();
				for (int l = 0; l < 100; ++l) {
					int i1 = event.getEntity().blockPosition().getX() + random.nextInt(16) - random.nextInt(16);
					int j1 =  event.getEntity().blockPosition().getY() + random.nextInt(16) - random.nextInt(16);
					int k1 =  event.getEntity().blockPosition().getZ() + random.nextInt(16) - random.nextInt(16);
					BlockState block = event.getEntity().level().getBlockState(new BlockPos(i1, j1, k1));

					if (block.isAir() && random.nextInt(config.y.get()) > j1) {
						event.getEntity().level().addParticle(ParticleTypes.ASH, i1 + random.nextFloat(), j1 + random.nextFloat(), k1 + random.nextFloat(), 0.0D, 0.0D, 0.0D);
					}
				}
			} else
				active = false;
		});
	}

	public static final ResourceKey<Level> WORLD_KEY_VOID = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath("voidscape", "void"));

	public boolean checkForVoidscapeDimension(Level world) {
		return config.voidscape.get() && world.dimension().location().equals(WORLD_KEY_VOID.location());
	}

}
