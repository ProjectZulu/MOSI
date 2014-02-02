package mosi.display.units.windows;

import javax.swing.GroupLayout.Alignment;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import com.google.common.base.Optional;
import com.google.gson.JsonObject;

import mosi.Log;
import mosi.display.DisplayHelper;
import mosi.display.DisplayRenderHelper;
import mosi.display.DisplayUnitFactory;
import mosi.display.units.DisplayUnit;
import mosi.display.units.DisplayUnit.ActionResult;
import mosi.display.units.DisplayUnit.HorizontalAlignment;
import mosi.display.units.DisplayUnit.MouseAction;
import mosi.display.units.DisplayUnit.ActionResult.SimpleAction;
import mosi.utilities.Coord;

/**
 * Interactive display which performs an action when CLICK is RELEASE.
 */
// TODO: Not finished. Still need clicked interface for command pattern
public class DisplayUnitButton implements DisplayUnit {
    public static final ResourceLocation guiButton = new ResourceLocation("mosi", "buttongui.png");
    public static final ResourceLocation guiIcons = new ResourceLocation("mosi", "icons.png");

    public static final String DISPLAY_ID = "DisplayUnitButton";
    private Coord offset;
    private Coord size;
    private boolean isClicked;
    private boolean isMouseOver;

    private Optional<String> displayText = Optional.of("CLOSE");

    private VerticalAlignment vertAlign;
    private HorizontalAlignment horizAlign;

    public static interface click {

    }

    public static class interaction {

    }

    public DisplayUnitButton(Coord offset, Coord size) {
        this.offset = offset;
        this.size = size;
        this.vertAlign = VerticalAlignment.BOTTOM_ABSO;
        this.horizAlign = HorizontalAlignment.CENTER_ABSO;
    }

    public DisplayUnitButton(Coord offset, Coord size, VerticalAlignment vertAlign, HorizontalAlignment horizAlign) {
        this.offset = offset;
        this.size = size;
        this.vertAlign = vertAlign;
        this.horizAlign = horizAlign;
    }

    @Override
    public String getType() {
        return DISPLAY_ID;
    }

    @Override
    public Coord getOffset() {
        return offset;
    }

    @Override
    public Coord getSize() {
        return size;
    }

    @Override
    public HorizontalAlignment getHorizontalAlignment() {
        return HorizontalAlignment.CENTER_ABSO;
    }

    @Override
    public VerticalAlignment getVerticalAlignment() {
        return VerticalAlignment.BOTTOM_ABSO;
    }

    @Override
    public void onUpdate(Minecraft mc, int ticks) {

    }

    @Override
    public boolean shouldRender(Minecraft mc) {
        return true;
    }

    @Override
    public void renderDisplay(Minecraft mc, Coord position) {
        FontRenderer fontRenderer = mc.fontRenderer;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.func_148821_a(770, 771, 1, 0);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        mc.getTextureManager().bindTexture(guiIcons);
        /* GUI Image */
        Coord iconCoord = new Coord(111, 2);
        Coord iconSize = new Coord(12, 16);
        DisplayRenderHelper.drawTexturedModalRect(Tessellator.instance, 1.0f, position.x + getSize().x / 2 - iconSize.x
                / 2, position.z + getSize().z / 2 - iconSize.z / 2, 111, 2, 12, 16);

        // DisplayRenderHelper.drawTexturedModalRect(Tessellator.instance, 1.0f, position.x + 3, position.z + 2, 111, 2,
        // 12, 16);
        mc.getTextureManager().bindTexture(guiButton);

        /* Background */
        if (isClicked) {
            DisplayRenderHelper.drawTexture4Quadrants(Tessellator.instance, -1.0f, position, getSize(), new Coord(128,
                    128), new Coord(127, 127));
        } else if (isMouseOver) {
            DisplayRenderHelper.drawTexture4Quadrants(Tessellator.instance, -1.0f, position, getSize(), new Coord(000,
                    0), new Coord(127, 127));
        } else {
            DisplayRenderHelper.drawTexture4Quadrants(Tessellator.instance, -0.1f, position, getSize(), new Coord(128,
                    0), new Coord(127, 127));
        }

        if (displayText.isPresent()) {
            String shortName = (String) fontRenderer.listFormattedStringToWidth(displayText.get(), getSize().x).get(0);
            // Note posZ-4+getSize/2. -4 is to 'center' the string vertically, and getSize/2 is to move center to the
            // middle button
            DisplayRenderHelper.drawCenteredString(fontRenderer, shortName, position.x + 1 + getSize().x / 2,
                    position.z - 4 + getSize().z / 2, 16777120, true);
        }
    }

    @Override
    public SimpleAction mousePosition(Coord localMouse) {
        isMouseOver = DisplayHelper.isCursorOverDisplay(localMouse, this);
        return isMouseOver ? ActionResult.SIMPLEACTION : ActionResult.NOACTION;
    }

    @Override
    public ActionResult mouseAction(Coord localMouse, MouseAction action, int... actionData) {
        switch (action) {
        case CLICK:
            // actionData[0] == EventButton, 0 == Left-Click, 1 == Right-Click
            if (actionData[0] == 0 && DisplayHelper.isCursorOverDisplay(localMouse, this)) {
                isClicked = true;
                return ActionResult.SIMPLEACTION;
            }
            break;
        case CLICK_MOVE:
            break;
        case RELEASE:
            if (isClicked) {
                // ReleaseAction();
            }
            isClicked = false;
            return ActionResult.NOACTION;
        }
        return ActionResult.NOACTION;
    }

    @Override
    public ActionResult keyTyped(char eventCharacter, int eventKey) {
        return ActionResult.NOACTION;
    }

    @Override
    public JsonObject saveCustomData(JsonObject jsonObject) {
        return null;
    }

    @Override
    public void loadCustomData(DisplayUnitFactory factory, JsonObject customData) {

    }
}
