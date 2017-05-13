package Tamaized.VoidFog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = VoidFog.modid, name = "VoidFog", version = VoidFog.version, clientSideOnly = true, acceptedMinecraftVersions = "[1.8,1.11.2]")
public class VoidFog {

	public final static String version = "${version}";
	public static final String modid = "voidfog";
	public static boolean voidcraft = false;

	@Instance(modid)
	public static VoidFog instance = new VoidFog();

	public static String getVersion() {
		return version;
	}

	public Logger logger;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = LogManager.getLogger(modid);
		if (Loader.isModLoaded("voidcraft")) {
			 logger.info("VoidCraft Detected.");
			 voidcraft = true;
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new FogEvent());
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}

}
