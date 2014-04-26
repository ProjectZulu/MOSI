package mosi.display;

import mosi.DisplayUnitRegistry;
import mosi.Log;
import mosi.MOSI;
import mosi.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.InputEvent.MouseInputEvent;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
    private enum GUI {
        UNKNOWN(-1), MAIN_SCREEN(0);
        public final int id;

        private GUI(int id) {
            this.id = id;
        }

        public static GUI idToGui(int id) {
            for (GUI gui : GUI.values()) {
                if (gui.id == id) {
                    return gui;
                }
            }
            return MAIN_SCREEN;
        }
    }

    private DisplayUnitRegistry displayRegistry;
    private Properties properties;

    public GuiHandler(DisplayUnitRegistry displayRegistry, Properties properties) {
        this.displayRegistry = displayRegistry;
        this.properties = properties;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (GUI.idToGui(ID)) {
        case MAIN_SCREEN:
            return new DisplayScreen(displayRegistry, properties);
        case UNKNOWN:
        default:
            return null;
        }
    }

    @SubscribeEvent
    public void keyPress(KeyInputEvent event) {
        if (properties.getOpenGuiKeyBind().func_151470_d()) {
            Minecraft.getMinecraft().thePlayer.openGui(MOSI.modInstance, GUI.MAIN_SCREEN.id,
                    Minecraft.getMinecraft().thePlayer.worldObj, 0, 0, 0);
        }
    }

    @SubscribeEvent
    public void mousePress(MouseInputEvent event) {
        if (properties.getOpenGuiKeyBind().func_151470_d()) {
            Minecraft.getMinecraft().thePlayer.openGui(MOSI.modInstance, GUI.MAIN_SCREEN.id,
                    Minecraft.getMinecraft().thePlayer.worldObj, 0, 0, 0);
        }
    }
}
