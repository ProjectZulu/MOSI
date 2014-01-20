package mosi.display;

import mosi.DisplayUnitRegistry;
import mosi.display.DisplayUnit.HorizontalAlignment;
import mosi.display.DisplayUnit.VerticalAlignment;
import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;

import com.google.common.collect.ImmutableList;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

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
    public void onRender(Post event) {
        if (event.type != null && event.type == ElementType.HOTBAR) {
            Minecraft mc = Minecraft.getMinecraft();
            ImmutableList<DisplayUnit> displayList = displayRegistry.currentDisplays();
            for (DisplayUnit displayUnit : displayList) {
                displayUnit.onUpdate(mc, inGameTicks);
                if (displayUnit.shouldRender(mc)) {
                    displayUnit.renderDisplay(mc, determineScreenPosition(mc, displayUnit));
                }
            }
            inGameTicks++;
        }
    }

    private Coord determineScreenPosition(Minecraft mc, DisplayUnit display) {
        ScaledResolution scaledResolition = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        Coord displaySize = display.getSize();
        Coord displayOffset = display.getPosition();
        VerticalAlignment vertAlign = display.getVerticalAlignment();
        HorizontalAlignment horizonAlign = display.getHorizontalAlignment();
        int horzCoord = getHorizontalCoord(horizonAlign, scaledResolition, displayOffset, displaySize);
        int vertCoord = getVerticalPosition(vertAlign, scaledResolition, displayOffset, displaySize);
        return new Coord(horzCoord, vertCoord);
    }

    private int getHorizontalCoord(HorizontalAlignment vertAlign, ScaledResolution resolution, Coord displayOffset,
            Coord displaySize) {
        int percOffset = (int) (resolution.getScaledWidth() * displayOffset.x / 100f);
        switch (vertAlign) {
        default:
        case LEFT_ABSO:
            return displayOffset.x;
        case LEFT_PERC:
            return percOffset;
        case CENTER_ABSO:
            return (resolution.getScaledWidth() / 2 - displaySize.x / 2) + displayOffset.x;
        case CENTER_PERC:
            return (resolution.getScaledWidth() / 2 - displaySize.x / 2) + percOffset;
        case RIGHT_ABSO:
            return (resolution.getScaledWidth() - displaySize.x) + displayOffset.x;
        case RIGHT_PERC:
            return (resolution.getScaledWidth() - displaySize.x) + percOffset;
        }
    }

    private int getVerticalPosition(VerticalAlignment vertAlign, ScaledResolution resolution, Coord displayOffset,
            Coord displaySize) {
        int percOffset = (int) (resolution.getScaledHeight() * displayOffset.z / 100f);
        switch (vertAlign) {
        default:
        case TOP_ABSO:
            return displayOffset.z;
        case TOP_PECR:
            return percOffset;
        case CENTER_ABSO:
            return (resolution.getScaledHeight() / 2 - displaySize.z / 2) + displayOffset.z;
        case CENTER_PERC:
            return (resolution.getScaledHeight() / 2 - displaySize.z / 2) + percOffset;
        case BOTTOM_ABSO:
            return (resolution.getScaledHeight() - displaySize.z) + displayOffset.z;
        case BOTTOM_PERC:
            return (resolution.getScaledHeight() - displaySize.z) + percOffset;
        }
    }
}
