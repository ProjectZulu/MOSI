package mosi.display;

import org.lwjgl.input.Keyboard;

import mosi.MOSI;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @SubscribeEvent
    public void key(KeyInputEvent event) {
        int desiredKey = Keyboard.KEY_0;
        if (Keyboard.getEventKey() == desiredKey && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            Minecraft.getMinecraft().thePlayer.openGui(MOSI.modInstance, 0,
                    Minecraft.getMinecraft().thePlayer.worldObj, 0, 0, 0);
        }
    }
}
