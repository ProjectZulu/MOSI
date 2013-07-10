package armorbarmod.client;

import net.minecraftforge.common.MinecraftForge;
import armorbarmod.common.CommonProxyArmorBarMod;
import armorbarmod.common.MOSIDisplayTicker;

public class ClientProxyArmorBarMod extends CommonProxyArmorBarMod {

    @Override
    public void registerDisplay() {
        MOSIDisplayTicker display = new MOSIDisplayTicker();
        MinecraftForge.EVENT_BUS.register(display);
    }
}
