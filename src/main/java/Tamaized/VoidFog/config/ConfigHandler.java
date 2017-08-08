package Tamaized.VoidFog.config;

import Tamaized.VoidFog.VoidFog;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.io.IOException;

public class ConfigHandler {

	private final File configFile;
	private final VoidFog mod;
	protected Configuration config;
	private boolean enabled = true;
	private boolean default_enabled = true;
	private boolean creative = true;
	private boolean default_creative = true;

	public ConfigHandler(VoidFog instance, File file, Configuration c) {
		mod = instance;
		configFile = file;
		config = c;
		config.load();
		init();
		sync(true);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public final Configuration getConfig() {
		return config;
	}

	public final void sync(boolean firstLoad) {
		try {
			loadData(firstLoad);
			cleanupFile();
			config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void cleanupFile() throws IOException {
		configFile.delete();
		configFile.createNewFile();
		config = new Configuration(configFile);
		cleanup();
	}

	@SubscribeEvent
	public void configChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(VoidFog.modid))
			sync(false);
	}

	protected void init() {
		enabled = true;
		default_enabled = true;
	}

	protected void loadData(boolean firstLoad) {
		enabled = this.config.get(Configuration.CATEGORY_GENERAL, "enabled", default_enabled).getBoolean();
		creative = this.config.get(Configuration.CATEGORY_GENERAL, "creative", default_creative).getBoolean();
	}

	protected void cleanup() throws IOException {
		config.get(Configuration.CATEGORY_GENERAL, "enabled", default_enabled).set(enabled);
		config.get(Configuration.CATEGORY_GENERAL, "creative", default_creative).set(creative);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isCreative() {
		return creative;
	}
}
