package mosi.proxy;

import mosi.DisplayUnitRegistry;
import mosi.display.DisplayTicker;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
    public void registerDisplayTicker(DisplayUnitRegistry displayRegistry) {
        MinecraftForge.EVENT_BUS.register(new DisplayTicker(displayRegistry));
    }
}
