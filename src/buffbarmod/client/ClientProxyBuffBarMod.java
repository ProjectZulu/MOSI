package buffbarmod.client;

import buffbarmod.common.BuffBarDisplayTicker;
import buffbarmod.common.CommonProxyBuffBarMod;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxyBuffBarMod extends CommonProxyBuffBarMod{

	@Override
	public void registerRenderThings() {
//		MinecraftForgeClient.preloadTexture("/mods/icons_BuffBarMod.png");
        TickRegistry.registerTickHandler(new BuffBarDisplayTicker(), Side.CLIENT);
	}
		
	//Pre-Init
	@Override
	public void registerTickers(){
		//None Yet :(
	}

}
