package mosi.proxy;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import mosi.DisplayUnitRegistry;
import mosi.MOSI;
import mosi.Properties;
import mosi.display.GuiHandler;

public class CommonProxy {

    public void registerDisplayTicker(DisplayUnitRegistry displayRegistry) {
    }

    public void registerGuiHandling(DisplayUnitRegistry displayRegistry, Properties properties) {
    }
}
