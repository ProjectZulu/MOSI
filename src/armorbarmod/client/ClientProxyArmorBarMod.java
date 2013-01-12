package armorbarmod.client;

import net.minecraftforge.client.MinecraftForgeClient;
import armorbarmod.common.CommonProxyArmorBarMod;
import armorbarmod.common.MOSIDisplayTicker;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxyArmorBarMod extends CommonProxyArmorBarMod{

	@Override
	public void registerRenderThings() {
		MinecraftForgeClient.preloadTexture("/armorbarresources/ArmorBarMod_Countdown.png");
	}
		
	//Pre-Init
	@Override
	public void registerTickers(){
		//None Yet :(
        TickRegistry.registerTickHandler(new MOSIDisplayTicker(), Side.CLIENT);
	}

}