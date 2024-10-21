package tamaized.voidfog;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import tamaized.beanification.Component;
import tamaized.beanification.PostConstruct;

import java.util.ArrayList;
import java.util.List;

@Component
public class Config {

	ModConfigSpec.IntValue y;
	ModConfigSpec.DoubleValue distance;
	ModConfigSpec.BooleanValue voidscape;

	ModConfigSpec.ConfigValue<List<? extends String>> blacklistedDims;
	ModConfigSpec.BooleanValue whitelistToggle;

	@PostConstruct
	private void postConstruct() {
		ModConfigSpec spec = new ModConfigSpec.Builder().configure(this::setup).getRight();
		ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.CLIENT, spec);
	}

	private Config setup(ModConfigSpec.Builder builder) {
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
				defineListAllowEmpty("dimension_blacklist", new ArrayList<>(), () -> "", s -> s instanceof String string && ResourceLocation.tryParse(string) != null);
		whitelistToggle = builder.
				translation("voidfog.config.whitelist_toggle").
				comment("Defines whether the dimension blacklist should function as a whitelist instead, meaning that only dimensions in that config option will render void fog.").
				define("toggle_whitelist", false);

		return this;
	}

}
