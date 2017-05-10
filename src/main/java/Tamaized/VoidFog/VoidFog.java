package Tamaized.VoidFog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = VoidFog.modid, name = "VoidFog", version = VoidFog.version, clientSideOnly = true, acceptedMinecraftVersions = "[1.8,1.11.2]")
public class VoidFog {

	public final static String version = "${version}";
	public static final String modid = "voidfog";

	@Instance(modid)
	public static VoidFog instance = new VoidFog();

	public static String getVersion() {
		return version;
	}

	public Logger logger;

	public void preInit(FMLPreInitializationEvent event) {
		logger = LogManager.getLogger(modid);
	}

	public void init(FMLInitializationEvent event) {

	}

	public void postInit(FMLPostInitializationEvent event) {

	}

}
