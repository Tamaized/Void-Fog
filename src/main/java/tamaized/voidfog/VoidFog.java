package tamaized.voidfog;

import net.neoforged.fml.common.Mod;
import tamaized.beanification.connector.fml.FMLBeanContextConnector;

@Mod(VoidFog.MODID)
public class VoidFog {

	public static final String MODID = "voidfog";

	static {
		FMLBeanContextConnector.init(MODID);
	}

	public VoidFog() {

	}


}
