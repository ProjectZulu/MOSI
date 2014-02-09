package mosi.display.units.windows;

import mosi.display.DisplayRenderHelper;
import mosi.display.resource.ImageResource;
import mosi.display.resource.SimpleImageResource.GuiButtonImageResource;
import mosi.display.units.DisplayUnit;
import mosi.display.units.DisplayUnitSettable;
import mosi.display.units.windows.DisplayUnitTextField.Validator;
import mosi.display.units.windows.DisplayUnitToggle.Toggle;
import mosi.utilities.Coord;
import mosi.utilities.StringHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

/**
 * Simple menu implementation: plain background screens which automatically resizes to fit its elements;
 */
public class DisplayWindowMenu extends DisplayWindow {
    public static final String DISPLAY_ID = "DisplayWindowMenu";
    private static final ResourceLocation widgets = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation guiButton = new ResourceLocation("mosi", "buttongui.png");

    private HorizontalAlignment horizAlign;
    private VerticalAlignment vertAlign;
    private Coord size;
    private ImageResource backgroundImage;

    public DisplayWindowMenu(Coord coord, HorizontalAlignment horizAlign, VerticalAlignment vertAlign) {
        super(coord);
        this.horizAlign = horizAlign;
        this.vertAlign = vertAlign;
        this.size = new Coord(20, 20);
        setDefaultImageResource();
    }

    public DisplayWindowMenu setBackgroundImage(ImageResource backgroundImage) {
        this.backgroundImage = backgroundImage;
        return this;
    }

    private final void setDefaultImageResource() {
        backgroundImage = new GuiButtonImageResource(new Coord(129, 000), new Coord(127, 127));
    }

    @Override
    public boolean addElement(DisplayUnit element) {
        if (super.addElement(element)) {
            int minX = 0;
            int maxX = 0;
            int minY = 0;
            int maxY = 0;
            for (DisplayUnit display : elements) {
                Coord offset = display.getOffset();
                Coord size = display.getSize();
                minX = Math.min(offset.x, minX);
                minX = Math.min(offset.x + size.x, minX);
                maxX = Math.max(offset.x, maxX);
                maxX = Math.max(offset.x + size.x, maxX);
                minY = Math.min(offset.z, minY);
                minY = Math.min(offset.z + size.z, minY);
                maxY = Math.max(offset.z, maxY);
                maxY = Math.max(offset.z + size.z, maxY);
            }
            size = new Coord(maxX - minX + 6, maxY - minY + 6);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeElement(DisplayUnit window) {
        if (super.removeElement(window)) {
            int minX = 0;
            int maxX = 0;
            int minY = 0;
            int maxY = 0;
            for (DisplayUnit display : elements) {
                Coord offset = display.getOffset();
                minX = Math.min(offset.x, minX);
                maxX = Math.max(offset.x, maxX);
                minY = Math.min(offset.z, minY);
                maxY = Math.max(offset.z, maxY);
            }
            size = new Coord(maxX - minX, maxY - minY);
            return true;
        } else {
            return false;
        }
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
    public HorizontalAlignment getHorizontalAlignment() {
        return horizAlign;
    }

    @Override
    public VerticalAlignment getVerticalAlignment() {
        return vertAlign;
    }

    @Override
    public void renderSubDisplay(Minecraft mc, Coord position) {
        FontRenderer fontrenderer = mc.fontRenderer;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.func_148821_a(770, 771, 1, 0);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        mc.getTextureManager().bindTexture(backgroundImage.getImageToBind()); // widgets
        DisplayRenderHelper.drawTexture4Quadrants(Tessellator.instance, -10.0f, position, getSize(),
                backgroundImage.getImageUV(), backgroundImage.getImageSize());
    }
}
