package Tamaized.VoidFog.config;

import Tamaized.VoidFog.VoidFog;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class ConfigGUI extends GuiConfig {

	public ConfigGUI(GuiScreen parent) {
		super(parent, new ConfigElement(VoidFog.config.getConfig().getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), VoidFog.modid, false, false, "VoidFog Config");
	}

	@Override
	public void initGui() {
		super.initGui();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
	}
}
