package mosi.display;

import mosi.DisplayUnitRegistry;
import mosi.MOSI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
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

    public GuiHandler(DisplayUnitRegistry displayRegistry) {
        this.displayRegistry = displayRegistry;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (GUI.idToGui(ID)) {
        case MAIN_SCREEN:
            return new DisplayScreen(displayRegistry);
        case UNKNOWN:
        default:
            return null;
        }
    }

    @SubscribeEvent
    public void keyPress(KeyInputEvent event) {
        int desiredKey = Keyboard.KEY_Y;
        if (Keyboard.getEventKey() == desiredKey && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            Minecraft.getMinecraft().thePlayer.openGui(MOSI.modInstance, GUI.MAIN_SCREEN.id,
                    Minecraft.getMinecraft().thePlayer.worldObj, 0, 0, 0);
        }
    }
}
