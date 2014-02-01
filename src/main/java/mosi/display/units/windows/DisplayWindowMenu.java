package mosi.display.units.windows;

import mosi.Log;
import mosi.display.DisplayHelper;
import mosi.display.DisplayRenderHelper;
import mosi.display.units.DisplayUnit.ActionResult.SimpleAction;
import mosi.display.units.DisplayUnitItem;
import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

/**
 * Simple menu implementation. It is essentially list of buttons/interactables
 */
public class DisplayWindowMenu extends DisplayWindow {
    public static final String DISPLAY_ID = "DisplayWindowMenu";
    private static final ResourceLocation widgets = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation guiButton = new ResourceLocation("mosi", "buttongui.png");

    // Parent to propagate changes to, this will probably be an interface of some sort later or a property of various
    // buttons/menu entries in the list
    private DisplayUnitItem parent;
    private HorizontalAlignment horizAlign;

    public DisplayWindowMenu(DisplayUnitItem parent) {
        this(parent, new Coord(0, 0), HorizontalAlignment.LEFT_ABSO);
    }

    public DisplayWindowMenu(DisplayUnitItem parent, Coord coord, HorizontalAlignment alignment) {
        super(coord);
        this.parent = parent;
        this.horizAlign = alignment;
        clearWindows();
        addWindow(new DisplayUnitButton(new Coord(-15, -2), new Coord(20, 10)));
        addWindow(new DisplayUnitButton(new Coord(+15, -2), new Coord(20, 10)));
    }

    @Override
    public String getSubType() {
        return DISPLAY_ID;
    }

    @Override
    public Coord getSize() {
        return new Coord(100, 50);
    }

    @Override
    public HorizontalAlignment getHorizontalAlignment() {
        return horizAlign;
    }

    @Override
    public VerticalAlignment getVerticalAlignment() {
        return VerticalAlignment.TOP_ABSO;
    }

    boolean isButtonUp = true;// Global, default true
    boolean isMouseOverButton = true; // global,
    int packedFGColour = 0; // Global public, occasionally set from outside

    @Override
    public SimpleAction mousePosition(Coord localMouse) {
        super.mousePosition(localMouse);
        isMouseOverButton = DisplayHelper.isCursorOverDisplay(localMouse, this);
        return new SimpleAction(isMouseOverButton);
    }

    @Override
    public void renderSubDisplay(Minecraft mc, Coord position) {
        FontRenderer fontrenderer = mc.fontRenderer;
        mc.getTextureManager().bindTexture(guiButton); // widgets
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.func_148821_a(770, 771, 1, 0);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        DisplayRenderHelper.drawTexture4Quadrants(Tessellator.instance, 0.5f, position, getSize(), new Coord(000, 128),
                new Coord(127, 127));

        // this.func_146119_b(mc, p_146112_2_, p_146112_3_); //Doesn't do anthing in GuiButton
        // int l = 14737632; // This is for changing the font color
        // if (packedFGColour != 0) {
        // l = packedFGColour;
        // } else if (!isButtonUp) {
        // l = 10526880;
        // } else if (this.isMouseOverButton) {
        // l = 16777120;
        // }
        // String field_146126_j = ""; //Text to display on button
        // drawCenteredString(fontrenderer, field_146126_j, field_146128_h + field_146120_f / 2, field_146129_i +
        // (field_146121_g - 8) / 2, l);
    }

    @Override
    public ActionResult mouseAction(Coord localMouse, MouseAction action, int... actionData) {
        return super.mouseAction(localMouse, action, actionData);
    }
}
