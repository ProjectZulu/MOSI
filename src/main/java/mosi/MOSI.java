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

@Mod(modid = DefaultProps.MOD_ID, name = DefaultProps.MOD_NAME, useMetadata = true)
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

    private static File configDirectory;

    public static File getConfigDirectory() {
        return configDirectory;
    }

    private static Properties properties;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        configDirectory = event.getModConfigurationDirectory();
        /** Configure Logging */
        Gson gson = GsonHelper.createGson(true);
        File loggingSettings = new File(event.getModConfigurationDirectory(), DefaultProps.MOD_DIR
                + "LoggingProperties.cfg");
        Log jasLog = GsonHelper.readOrCreateFromGson(FileUtilities.createReader(loggingSettings, false), Log.class,
                gson);
        Log.setLogger(jasLog);
        GsonHelper.writeToGson(FileUtilities.createWriter(loggingSettings, true), jasLog, gson);

        /** Configure Generic Settings */
        properties = new Properties(event.getModConfigurationDirectory());
        properties.loadFromConfig();

        /** Configure Displays */
        displayFactory = new DisplayUnitFactory();
        displayRegistry = new DisplayUnitRegistry(displayFactory, event.getModConfigurationDirectory());
        proxy.registerDisplayTicker(displayRegistry);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.registerGuiHandling(displayRegistry, properties);
    }
}
