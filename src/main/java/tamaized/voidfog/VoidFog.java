package tamaized.voidfog;

import net.neoforged.fml.common.Mod;
import tamaized.beanification.BeanContext;

@Mod(VoidFog.MODID)
public class VoidFog {

	public static final String MODID = "voidfog";

	static {
		BeanContext.configure()
			.configurableSettings().disableRegistry()
			.configurableSettings().disableRenderer()
			.configurableSettings().disableEntity();
		BeanContext.init();
	}

	public VoidFog() {

	}


}
