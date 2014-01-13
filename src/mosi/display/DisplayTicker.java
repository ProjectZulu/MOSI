package mosi.display;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.util.Point;

/**
 * Passive Displaying of GuiDisplays in Game
 */
public class DisplayTicker {
    private int inGameTicks = 0;
    protected float zLevel = 10.0F;

    private List<DisplayUnit> displayList = new ArrayList<DisplayUnit>();

    public void addDisplay(DisplayUnit displayUnit) {
        displayList.add(displayUnit);
    }

    @ForgeSubscribe
    public void onRender(Post event) {
        if (event.type != null && event.type == ElementType.HOTBAR) {
            Minecraft mc = Minecraft.getMinecraft();
            for (DisplayUnit displayUnit : displayList) {
                displayUnit.onUpdate(mc, inGameTicks);
                if (displayUnit.shouldRender(mc)) {
                    displayUnit.renderDisplay(mc, new Point(0, 0));
                }
            }
            inGameTicks++;
        }
    }
}
