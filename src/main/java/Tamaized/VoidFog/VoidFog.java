package Tamaized.VoidFog;

import Tamaized.VoidFog.config.ConfigHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = VoidFog.modid, name = "VoidFog", version = VoidFog.version, clientSideOnly = true, guiFactory = "Tamaized.VoidFog.config.GUIConfigFactory", acceptedMinecraftVersions = "[1.8,1.12]")
public class VoidFog {

	public final static String version = "${version}";
	public static final String modid = "voidfog";
	public static boolean voidcraft = false;
	@Instance(modid)
	public static VoidFog instance = new VoidFog();
	public Logger logger;
	public static ConfigHandler config;

	public static String getVersion() {
		return version;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = LogManager.getLogger(modid);
		File file = event.getSuggestedConfigurationFile();
		config = new ConfigHandler(this, file, new Configuration(file));
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
