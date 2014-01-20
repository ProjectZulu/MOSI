package buffbarmod.client;

import buffbarmod.common.CommonProxyBuffBarMod;

public class ClientProxyBuffBarMod extends CommonProxyBuffBarMod{

	@Override
	public void registerRenderThings() {
		// MinecraftForgeClient.preloadTexture("/mods/icons_BuffBarMod.png");
		// In 1.7 TickRegistry and TickHandlers were replaced with TickEvent
		// TickRegistry.registerTickHandler(new BuffBarDisplayTicker(),
		// Side.CLIENT);
	}
		
	//Pre-Init
	@Override
	public void registerTickers(){
		//None Yet :(
	}

}
