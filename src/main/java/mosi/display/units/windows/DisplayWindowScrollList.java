package mosi.display.units.windows;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import mosi.Log;
import mosi.display.DisplayHelper;
import mosi.display.DisplayRenderHelper;
import mosi.display.inventoryrules.InventoryRules;
import mosi.display.inventoryrules.ItemHandMatch;
import mosi.display.inventoryrules.ItemIdMatch;
import mosi.display.inventoryrules.ItemMetaMatch;
import mosi.display.inventoryrules.ItemSlotMatch;
import mosi.display.units.DisplayUnit;
import mosi.display.units.DisplayUnitInventoryRule;
import mosi.display.units.DisplayUnitItem;
import mosi.display.units.DisplayUnit.HorizontalAlignment;
import mosi.display.units.DisplayUnit.VerticalAlignment;
import mosi.display.units.DisplayUnit.ActionResult.SimpleAction;
import mosi.display.units.DisplayUnitSettable;
import mosi.display.units.windows.DisplayWindowMenu.PositionTextValidator;
import mosi.display.units.windows.DisplayWindowSlider.Sliden;
import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

/**
 * Vertical scrolling list that handles placement of a list of renderable DisplayUnits based on their sizes
 * 
 * List displays positions are set based on the scroll percentage. Their alignment is also set to LEFT and TOP
 */
public class DisplayWindowScrollList extends DisplayWindow implements Sliden {
    public static final String DISPLAY_ID = "DisplayWindowMenu";
    private static final ResourceLocation guiButton = new ResourceLocation("mosi", "buttongui.png");

    private HorizontalAlignment horizAlign;
    private VerticalAlignment vertAlign;

    private int headerSize = 20;
    private int scrollLength;
    private DisplayWindowSlider slider;
    private int scrolledDistance;
    private Coord size;
    private Scrollable scrollable;

    @Override
    public void setScrollDistance(int scrollDistance, int scrollLength) {
        this.scrolledDistance = scrollDistance;
    }

    // TODO: Should this implement Sliden Slider interface?
    public static interface Scrollable {
        public abstract Collection<? extends ScrollableElement> getElements();

        public abstract boolean removeElement(ScrollableElement element);
    }

    public static interface ScrollableElement extends DisplayUnitSettable {
        public abstract void setScrollVisibity(boolean visibility);

        public abstract boolean isVisibleInScroll();
    }

    public DisplayWindowScrollList(Coord offset, Coord size, int headerSize, VerticalAlignment vertAlign,
            HorizontalAlignment horizAlign, Scrollable scrollable) {
        super(offset);
        this.horizAlign = horizAlign;
        this.vertAlign = vertAlign;
        this.size = size;
        this.headerSize = Math.abs(headerSize);
        int sliderWidth = 15;
        this.scrollLength = Math.abs(size.z - (this.headerSize * 2)) - sliderWidth;
        this.slider = new DisplayWindowSlider(new Coord(0, this.headerSize), new Coord(sliderWidth, sliderWidth),
                this.scrollLength, true, VerticalAlignment.TOP_ABSO, HorizontalAlignment.RIGHT_ABSO, this);
        this.scrollable = scrollable;
    }

    @Override
    public String getSubType() {
        return DISPLAY_ID;
    }

    @Override
    public Coord getSize() {
        return size;
    }

    @Override
    public VerticalAlignment getVerticalAlignment() {
        return vertAlign;
    }

    @Override
    public HorizontalAlignment getHorizontalAlignment() {
        return horizAlign;
    }

    @Override
    public void onUpdate(Minecraft mc, int ticks) {
        slider.onUpdate(mc, ticks);
        int listSize = 0;
        Collection<? extends ScrollableElement> scrollDisplays = scrollable.getElements();
        for (DisplayUnit element : scrollDisplays) {
            listSize += element.getSize().z;
        }
        listSize = listSize - (getSize().z - headerSize * 2);
        int elementPosY = 0;
        for (DisplayUnitSettable element : scrollDisplays) {
            element.setHorizontalAlignment(HorizontalAlignment.LEFT_ABSO);
            element.setVerticalAlignment(VerticalAlignment.TOP_ABSO);
            /*
             * Do not include bottom of list; when scroll bar reached bottom, the bottom element should be at bottom of
             * screen not top
             */
            float scrollPerc = scrolledDistance * 1f / scrollLength;
            int zCoord = headerSize + elementPosY - (int) (scrollPerc * listSize);
            elementPosY += element.getSize().z;
            element.setOffset(new Coord(0, zCoord));
        }
        for (DisplayUnitSettable element : scrollDisplays) {
            element.onUpdate(mc, ticks);
        }

        super.onUpdate(mc, ticks);
    }

    @Override
    public void renderSubDisplay(Minecraft mc, Coord position) {
        FontRenderer fontrenderer = mc.fontRenderer;
        mc.getTextureManager().bindTexture(guiButton);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.func_148821_a(770, 771, 1, 0);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        DisplayRenderHelper.drawTexture4Quadrants(Tessellator.instance, -5.0f, position, getSize(),
                new Coord(000, 128), new Coord(127, 127));

        for (ScrollableElement element : scrollable.getElements()) {
            Coord elemPos = DisplayHelper.determineScreenPositionFromDisplay(mc, position, getSize(), element);
            // Should scroll visibility logic be done during rendering? Can it be done without absolute position?
            if (elemPos.z > position.z && elemPos.z < position.z + getSize().z - element.getSize().z) {
                element.setScrollVisibity(true);
                element.renderDisplay(mc, elemPos);
            } else {
                element.setScrollVisibity(false);
            }
        }

        mc.getTextureManager().bindTexture(guiButton);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.func_148821_a(770, 771, 1, 0);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        DisplayRenderHelper.drawTexture4Quadrants(Tessellator.instance, -5.0f, position, new Coord(getSize().x,
                headerSize), new Coord(0, 0), new Coord(127, 127));
        DisplayRenderHelper.drawTexture4Quadrants(Tessellator.instance, -5.0f,
                position.add(0, getSize().z + 1 - headerSize), new Coord(getSize().x, headerSize), new Coord(0, 0),
                new Coord(127, 127));

        slider.renderDisplay(mc, DisplayHelper.determineScreenPositionFromDisplay(mc, position, getSize(), slider));
    }

    @Override
    public SimpleAction mousePosition(Coord localMouse) {
        if (slider.mousePosition(DisplayHelper.localizeMouseCoords(Minecraft.getMinecraft(), localMouse, this, slider)).stopActing) {
            return ActionResult.SIMPLEACTION;
        }

        for (ScrollableElement element : scrollable.getElements()) {
            if (element.isVisibleInScroll()) {
                if (element.mousePosition(DisplayHelper.localizeMouseCoords(Minecraft.getMinecraft(), localMouse, this,
                        element)).stopActing) {
                    return ActionResult.SIMPLEACTION;
                }
            }
        }

        return super.mousePosition(localMouse);
    }

    @Override
    public ActionResult mouseAction(Coord localMouse, MouseAction action, int... actionData) {
        if (slider.mouseAction(DisplayHelper.localizeMouseCoords(Minecraft.getMinecraft(), localMouse, this, slider),
                action, actionData).shouldStop()) {
            return ActionResult.SIMPLEACTION;
        }

        for (ScrollableElement element : scrollable.getElements()) {
            if (element.isVisibleInScroll()) {
                if (element.mouseAction(
                        DisplayHelper.localizeMouseCoords(Minecraft.getMinecraft(), localMouse, this, element), action,
                        actionData).shouldStop()) {
                    return ActionResult.SIMPLEACTION;
                }
            }
        }

        return super.mouseAction(localMouse, action, actionData);
    }

    @Override
    public ActionResult keyTyped(char eventCharacter, int eventKey) {
        if (slider.keyTyped(eventCharacter, eventKey).shouldStop()) {
            return ActionResult.SIMPLEACTION;
        }

        for (ScrollableElement element : scrollable.getElements()) {
            if (element.isVisibleInScroll()) {
                if (element.keyTyped(eventCharacter, eventKey).shouldStop()) {
                    return ActionResult.SIMPLEACTION;
                }
            }
        }
        return super.keyTyped(eventCharacter, eventKey);
    }

}
