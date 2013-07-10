package armorbarmod.common;

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

@Mod(modid = DefaultProps.modId, name = "Armor Bar Mod", version = DefaultProps.VERSION_STRING)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class mod_ArmorBarMod {

    @Instance(DefaultProps.modId)
    public static mod_ArmorBarMod modInstance;

    @SidedProxy(clientSide = "armorbarmod.client.ClientProxyArmorBarMod", serverSide = "armorbarmod.common.CommonProxyArmorBarMod")
    public static CommonProxyArmorBarMod proxy;

    @PreInit
    public void preInit(FMLPreInitializationEvent event) {
        Configuration armorConfig = new Configuration(event.getSuggestedConfigurationFile());
        armorConfig.load();
        DisplayBuilder.loadDisplayFromConfig(armorConfig);
        armorConfig.save();
        proxy.registerDisplay();
    }

    @Init
    public void load(FMLInitializationEvent event) {
        DisplayBuilder.buildDisplay();
    }

    @PostInit
    public void postInit(FMLPostInitializationEvent event) {
    }
}
