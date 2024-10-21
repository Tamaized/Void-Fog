package tamaized.voidfog;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import tamaized.beanification.Autowired;
import tamaized.beanification.Component;

@Component
public class DimensionUtil {

	@Autowired
	private Config config;

	public final ResourceKey<Level> WORLD_KEY_VOID = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath("voidscape", "void"));

	public boolean checkForVoidscapeDimension(Level world) {
		return config.voidscape.get() && world.dimension().location().equals(WORLD_KEY_VOID.location());
	}

}
