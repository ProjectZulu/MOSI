package mosi;

import java.io.File;

import mosi.display.DisplayUnitFactory;
import mosi.proxy.CommonProxy;
import mosi.utilities.FileUtilities;
import mosi.utilities.GsonHelper;

import com.google.gson.Gson;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = DefaultProps.MOD_ID, name = DefaultProps.MOD_NAME, version = DefaultProps.VERSION)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class MOSI {
    @Instance(DefaultProps.MOD_ID)
    public static MOSI modInstance;

    @SidedProxy(clientSide = "mosi.proxy.ClientProxy", serverSide = "mosi.proxy.CommonProxy")
    public static CommonProxy proxy;

    DisplayUnitFactory displayFactory;
    DisplayUnitRegistry displayRegistry;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Gson gson = GsonHelper.createGson(true);
        File loggingSettings = new File(event.getModConfigurationDirectory(), DefaultProps.MOD_DIR
                + "LoggingProperties.cfg");
        Log jasLog = GsonHelper.readFromGson(FileUtilities.createReader(loggingSettings, false), Log.class, gson);
        Log.setLogger(jasLog);
        displayFactory = new DisplayUnitFactory();
        displayRegistry = new DisplayUnitRegistry(displayFactory);
        proxy.registerDisplayTicker(displayRegistry);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }
}
