package mosi.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import mosi.DisplayUnitRegistry;
import mosi.MOSI;
import mosi.display.DisplayTicker;
import mosi.display.GuiHandler;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
    @Override
    public void registerDisplayTicker(DisplayUnitRegistry displayRegistry) {
        MinecraftForge.EVENT_BUS.register(new DisplayTicker(displayRegistry));
    }

    @Override
    public void registerGuiHandling() {
        GuiHandler guiHandler = new GuiHandler();
        NetworkRegistry.INSTANCE.registerGuiHandler(MOSI.modInstance, guiHandler);
        MinecraftForge.EVENT_BUS.register(guiHandler);
    }
}
