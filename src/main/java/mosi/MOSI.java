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
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = DefaultProps.MOD_ID, name = DefaultProps.MOD_NAME, version = DefaultProps.VERSION)
public class MOSI {
    @Instance(DefaultProps.MOD_ID)
    public static MOSI modInstance;

    @SidedProxy(clientSide = "mosi.proxy.ClientProxy", serverSide = "mosi.proxy.CommonProxy")
    public static CommonProxy proxy;

    private DisplayUnitRegistry displayRegistry;
    private static DisplayUnitFactory displayFactory;

    public static DisplayUnitFactory getDisplayFactory() {
        return displayFactory;
    }

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
}
