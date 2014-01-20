package buffbarmod.common;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "mod_BuffBarMod", name = "Buff Bar Mod", version = "0.7.1")
//@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class mod_BuffBarMod {
	
	@Instance("mod_BuffBarMod")
	public static mod_BuffBarMod modInstance;
	
	@SidedProxy(clientSide = "buffbarmod.client.ClientProxyBuffBarMod", serverSide = "buffbarmod.common.CommonProxyBuffBarMod")
	public static CommonProxyBuffBarMod proxy;
	
	public static int xOffset = 10;
	public static int yOffset = -28;
	public static int displayType = 0;
	public static int fontColor = 1030655;
	public static int creativeYOffSet = 13;
	public static int analogMaxDurationLength = 60;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
//		proxy.registerTickers();
//        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
//        config.load();
//        
//        xOffset = config.get("Buff Bar Controls", "Display X Offeset", xOffset).getInt(xOffset);
//        yOffset = config.get("Buff Bar Controls", "Display Y Offeset", yOffset).getInt(yOffset);
//        displayType = config.get("Buff Bar Controls", "Display Type", displayType).getInt(displayType);
//        fontColor = config.get("Buff Bar Controls", "Display Font Color", fontColor).getInt(fontColor);
//        creativeYOffSet = config.get("Buff Bar Controls", "Display YOffSet in Creative", creativeYOffSet).getInt(creativeYOffSet);
//        analogMaxDurationLength = config.get("Buff Bar Controls", "Analog Max Duration Length", analogMaxDurationLength).getInt(analogMaxDurationLength);
//        
//        config.save();
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
//		proxy.registerRenderThings();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		
	}

	public mod_BuffBarMod(){
		
	}
}
