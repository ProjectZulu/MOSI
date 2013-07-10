package armorbarmod.common;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.event.ForgeSubscribe;

public class MOSIDisplayTicker {
    public static int inGameTicks = 0;
    protected float zLevel = 10.0F;

    private static List<DisplayUnit> displayList = new ArrayList<DisplayUnit>();

    public static void addDisplay(DisplayUnit displayUnit) {
        displayList.add(displayUnit);
    }

    @ForgeSubscribe
    public void onRender(Post event) {
        if (event.type != null && event.type == ElementType.HOTBAR) {
            Minecraft mc = Minecraft.getMinecraft();
            for (DisplayUnit displayUnit : displayList) {
                displayUnit.onUpdate(mc, inGameTicks);
                if (displayUnit.shouldRender(mc)) {
                    displayUnit.renderDisplay(mc);
                }
            }
            inGameTicks++;
        }
    }
}
