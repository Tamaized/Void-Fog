package tamaized.voidfog;

import net.neoforged.fml.common.Mod;
import tamaized.beanification.BeanContext;

@Mod(VoidFog.MODID)
public class VoidFog {

	public static final String MODID = "voidfog";

	static {
		BeanContext.init();
	}

	public VoidFog() {
		BeanContext.enableMainModClassInjections(this);

	}


}
