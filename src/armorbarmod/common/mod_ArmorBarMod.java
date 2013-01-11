package armorbarmod.common;

import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "mod_ArmorBarMod", name = "Armor Bar Mod", version = "0.5.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)

public class mod_ArmorBarMod {
	
	@Instance("mod_ArmorBarMod")
	public static mod_ArmorBarMod modInstance;
	
	@SidedProxy(clientSide = "armorbarmod.client.ClientProxyArmorBarMod", serverSide = "armorbarmod.common.CommonProxyArmorBarMod")
	public static CommonProxyArmorBarMod proxy;

	
	@PreInit
	public void preInit(FMLPreInitializationEvent event){
        Configuration armorConfig = new Configuration(event.getSuggestedConfigurationFile());
        armorConfig.load();
        DisplayBuilder.loadDisplayFromConfig(armorConfig);
        armorConfig.save();
		proxy.registerTickers();
	}
	
	@Init
	public void load(FMLInitializationEvent event){
        DisplayBuilder.buildDisplay();
		proxy.registerRenderThings();
	}
	
	@PostInit
	public void postInit(FMLPostInitializationEvent event){

		
	}
}
