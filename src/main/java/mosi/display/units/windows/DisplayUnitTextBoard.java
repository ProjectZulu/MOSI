package mosi.display.units.windows;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import scala.Array;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;

import com.google.gson.JsonObject;

import mosi.display.DisplayRenderHelper;
import mosi.display.DisplayUnitFactory;
import mosi.display.resource.ImageResource;
import mosi.display.resource.SimpleImageResource.GuiButtonImageResource;
import mosi.display.units.DisplayUnit;
import mosi.display.units.DisplayUnit.ActionResult.SimpleAction;
import mosi.utilities.Coord;

/**
 * Simple Text board which resizes to fit provided text
 */
public class DisplayUnitTextBoard implements DisplayUnit {
    public static final String DISPLAY_ID = "DisplayUnitTextField";
    private Coord offset;
    private Coord size;
    private VerticalAlignment vertAlign;
    private HorizontalAlignment horizAlign;
    private ImageResource backgroundImage;
    private ArrayList<String> displayText;

    public DisplayUnitTextBoard(Coord offset, VerticalAlignment vertAlign, HorizontalAlignment horizAlign,
            String... displayText) {
        this.offset = offset;
        this.vertAlign = vertAlign;
        this.horizAlign = horizAlign;
        this.displayText = new ArrayList<String>(displayText.length + 2);
        final int maxLineLength = 239; // Larger strings causes background issues withdrawing pattern, should be
                                       // configurable
        for (String displayLine : displayText) {
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
            for (Object nonGenericString : fontRenderer.listFormattedStringToWidth(displayLine, maxLineLength)) {
                this.displayText.add((String) nonGenericString);
            }
        }
        this.size = calculateSize();
        setDefaultImageResource();
    }

    private final void setDefaultImageResource() {
        backgroundImage = new GuiButtonImageResource(new Coord(129, 000), new Coord(127, 127));
    }

    private final Coord calculateSize() {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        int widestLine = 10; // Minimum width
        for (String displayLine : displayText) {
            int width = fontRenderer.getStringWidth(displayLine);
            if (width > widestLine) {
                widestLine = width;
            }
        }
        // length + 1 To allow for some clearance room
        return new Coord(widestLine + 10, fontRenderer.FONT_HEIGHT * (displayText.size() + 1));
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
    public VerticalAlignment getVerticalAlignment() {
        return vertAlign;
    }

    @Override
    public HorizontalAlignment getHorizontalAlignment() {
        return horizAlign;
    }

    @Override
    public void onUpdate(Minecraft mc, int ticks) {
        this.size = calculateSize();
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
        mc.getTextureManager().bindTexture(backgroundImage.getImageToBind()); // widgets
        DisplayRenderHelper.drawTexture4Quadrants(Tessellator.instance, -10.0f, position, getSize(),
                backgroundImage.getImageUV(), backgroundImage.getImageSize());

        for (int i = 0; i < displayText.size(); i++) {
            String displayLine = displayText.get(i);
            DisplayRenderHelper.drawString(fontRenderer, displayLine, position.x + 5, position.z + fontRenderer.FONT_HEIGHT / 2 + i
                    * fontRenderer.FONT_HEIGHT, 16777120, true);
        }
    }

    @Override
    public SimpleAction mousePosition(Coord localMouse) {
        return ActionResult.NOACTION;
    }

    @Override
    public ActionResult mouseAction(Coord localMouse, MouseAction action, int... actionData) {
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
