package mosi;

import java.io.File;

import mosi.utilities.FileUtilities;
import mosi.utilities.GsonHelper;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import cpw.mods.fml.client.registry.ClientRegistry;

public class Properties {
    private final String GUI_KEYBIND_KEY = "OPEN_GUI_KEYBIND";
    private final AutoSaveKeyBinding openGuiKeyBind;
    private final File configDirectory;

    public KeyBinding getOpenGuiKeyBind() {
        return openGuiKeyBind;
    }

    /**
     * KeyBinding that has a save-callback whenever the KeyCode is changed
     */
    private static class AutoSaveKeyBinding extends KeyBinding {
        private Properties properties;

        public AutoSaveKeyBinding(String keyDescription, int keyCode, String category, Properties properties) {
            super(keyDescription, keyCode, category);
            this.properties = properties;
        }

        @Override
        public void func_151462_b(int p_151462_1_) {
            super.func_151462_b(p_151462_1_);
            properties.saveToConfig();
        }
    }

    public Properties(File configDirectory) {
        openGuiKeyBind = new AutoSaveKeyBinding("key.mosi.opengui", Keyboard.KEY_Y, "key.categories.misc", this);
        ClientRegistry.registerKeyBinding(openGuiKeyBind);
        this.configDirectory = configDirectory;
    }

    public void loadFromConfig() {
        File file = new File(configDirectory, DefaultProps.MOD_DIR + "Properties.cfg");
        Gson gson = GsonHelper.createGson(true);
        JsonObject saveSettings = GsonHelper.readFromGson(FileUtilities.createReader(file, false), JsonObject.class,
                gson);
        int keyCode = GsonHelper.getMemberOrDefault(saveSettings, GUI_KEYBIND_KEY, Keyboard.KEY_Y);
        openGuiKeyBind.func_151462_b(keyCode);
    }

    public void saveToConfig() {
        Gson gson = GsonHelper.createGson(true);
        File file = new File(configDirectory, DefaultProps.MOD_DIR + "Properties.cfg");
        JsonObject saveSettings = new JsonObject();
        saveSettings.addProperty(GUI_KEYBIND_KEY, openGuiKeyBind.func_151463_i());
        GsonHelper.writeToGson(FileUtilities.createWriter(file, true), saveSettings, gson);
    }
}
