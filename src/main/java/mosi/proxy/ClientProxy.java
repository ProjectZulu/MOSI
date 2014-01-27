package mosi.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
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
        DisplayTicker ticker = new DisplayTicker(displayRegistry);
        MinecraftForge.EVENT_BUS.register(ticker);
        FMLCommonHandler.instance().bus().register(ticker);
    }

    @Override
    public void registerGuiHandling(DisplayUnitRegistry displayRegistry) {
        GuiHandler guiHandler = new GuiHandler(displayRegistry);
        NetworkRegistry.INSTANCE.registerGuiHandler(MOSI.modInstance, guiHandler);
        FMLCommonHandler.instance().bus().register(guiHandler);
    }
}
