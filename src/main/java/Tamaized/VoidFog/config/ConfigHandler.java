package Tamaized.VoidFog.config;

import Tamaized.VoidFog.VoidFog;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
@Config(modid = VoidFog.modid)
public class ConfigHandler {

	@Config.Comment("Enable")
	public static boolean enabled = true;

	@Config.Comment("Disable Fog while in Creative or Spectator mode")
	public static boolean creative = true;

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(VoidFog.modid)) {
			ConfigManager.sync(VoidFog.modid, Config.Type.INSTANCE);
		}
	}

}
