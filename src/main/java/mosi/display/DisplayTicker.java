package mosi.display;

import org.lwjgl.opengl.GL11;

import mosi.DisplayUnitRegistry;
import mosi.display.units.DisplayUnit;
import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;

import com.google.common.collect.ImmutableList;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;

/**
 * Passive Displaying of GuiDisplays in Game
 */
public class DisplayTicker {
    private int inGameTicks = 0;
    private DisplayUnitRegistry displayRegistry;

    public DisplayTicker(DisplayUnitRegistry displayRegistry) {
        this.displayRegistry = displayRegistry;
    }

    @SubscribeEvent
    public void onUpdate(ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        // thePlayer != null == We are in the world, !currentScreen.doesGuiPauseGame == World logic is passing
        if (mc.thePlayer != null && (mc.currentScreen == null || !mc.currentScreen.doesGuiPauseGame())) {
            ImmutableList<DisplayUnit> displayList = displayRegistry.currentDisplays();
            for (DisplayUnit displayUnit : displayList) {
                displayUnit.onUpdate(mc, inGameTicks);
            }
        }
    }

    @SubscribeEvent
    public void onRender(Post event) {
        if (event.type != null && event.type == ElementType.HOTBAR) {
            Minecraft mc = Minecraft.getMinecraft();
            ImmutableList<DisplayUnit> displayList = displayRegistry.currentDisplays();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            for (DisplayUnit displayUnit : displayList) {
                if (displayUnit.shouldRender(mc)) {
                    ScaledResolution scaledResolition = new ScaledResolution(mc.gameSettings, mc.displayWidth,
                            mc.displayHeight);
                    Coord screenPos = DisplayHelper.determineScreenPositionFromDisplay(mc, new Coord(0, 0), new Coord(
                            scaledResolition.getScaledWidth(), scaledResolition.getScaledHeight()), displayUnit);
                    displayUnit.renderDisplay(mc, screenPos);
                }
            }
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            inGameTicks++;
        }
    }
}
