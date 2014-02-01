package mosi.display.units.windows;

import javax.swing.GroupLayout.Alignment;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

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

public class DisplayUnitButton implements DisplayUnit {
    private static final ResourceLocation guiButton = new ResourceLocation("mosi", "buttongui.png");

    public static final String DISPLAY_ID = "DisplayUnitButton";
    private Coord offset;
    private Coord size;
    private boolean isClicked;
    private boolean isMouseOver;

    public DisplayUnitButton(Coord offset, Coord size) {
        this.offset = offset;
        this.size = size;
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
        FontRenderer fontrenderer = mc.fontRenderer;
        mc.getTextureManager().bindTexture(guiButton); // widgets
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.func_148821_a(770, 771, 1, 0);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (isClicked) {
            DisplayRenderHelper.drawTexture4Quadrants(Tessellator.instance, 1.0f, position, getSize(), new Coord(128,
                    128), new Coord(127, 127));
        } else if (isMouseOver) {
            DisplayRenderHelper.drawTexture4Quadrants(Tessellator.instance, 1.0f, position, getSize(),
                    new Coord(000, 0), new Coord(127, 127));
        } else {
            DisplayRenderHelper.drawTexture4Quadrants(Tessellator.instance, 1.0f, position, getSize(),
                    new Coord(128, 0), new Coord(127, 127));
        }
    }

    @Override
    public SimpleAction mousePosition(Coord localMouse) {
//        Log.log().info("Butn: {%s} localMouse {%s}", getOffset(), localMouse);
        isMouseOver = DisplayHelper.isCursorOverDisplay(localMouse, this);
        return new SimpleAction(isMouseOver);
    }

    @Override
    public ActionResult mouseAction(Coord localMouse, MouseAction action, int... actionData) {
        switch (action) {
        case CLICK:
            // actionData[0] == EventButton, 0 == Left-Click, 1 == Right-Click
            if (actionData[0] == 0 && DisplayHelper.isCursorOverDisplay(localMouse, this)) {
                isClicked = true;
                return new ActionResult(true);
            }
            break;
        case CLICK_MOVE:
            break;
        case RELEASE:
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
