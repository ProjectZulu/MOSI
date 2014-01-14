package mosi.display;

import mosi.DisplayUnitRegistry;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.util.Point;

import com.google.common.collect.ImmutableList;

/**
 * Passive Displaying of GuiDisplays in Game
 */
public class DisplayTicker {
    private int inGameTicks = 0;
    private DisplayUnitRegistry displayRegistry;

    public DisplayTicker(DisplayUnitRegistry displayRegistry) {
        this.displayRegistry = displayRegistry;
    }

    @ForgeSubscribe
    public void onRender(Post event) {
        if (event.type != null && event.type == ElementType.HOTBAR) {
            Minecraft mc = Minecraft.getMinecraft();
            ImmutableList<DisplayUnit> displayList = displayRegistry.currentDisplays();
            for (DisplayUnit displayUnit : displayList) {
                displayUnit.onUpdate(mc, inGameTicks);
                if (displayUnit.shouldRender(mc)) {
                    displayUnit.renderDisplay(mc, displayUnit.getPosition());
                }
            }
            inGameTicks++;
        }
    }
}
