package mosi.display.units.windows;

import mosi.display.DisplayHelper;
import mosi.display.DisplayRenderHelper;
import mosi.display.units.DisplayUnit.ActionResult.SimpleAction;
import mosi.display.units.DisplayUnitSettable;
import mosi.display.units.windows.DisplayUnitToggle.Toggle;
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
    private DisplayUnitSettable parent;
    private HorizontalAlignment horizAlign;
    private VerticalAlignment vertAlign;

    public DisplayWindowMenu(DisplayUnitSettable parent) {
        // TODO: Alignment and pos should be inferred from parent
        this(parent, new Coord(0, 0), HorizontalAlignment.LEFT_ABSO, VerticalAlignment.TOP_ABSO);
    }

    public DisplayWindowMenu(DisplayUnitSettable parent, Coord coord, HorizontalAlignment horizAlign, VerticalAlignment vertAlign) {
        super(parent.getOffset());
        this.parent = parent;
        this.horizAlign = parent.getHorizontalAlignment();
        this.vertAlign = parent.getVerticalAlignment();
        clearWindows();

        addWindow(new DisplayUnitToggle(new Coord(-25, 3), new Coord(20, 20), VerticalAlignment.TOP_ABSO,
                HorizontalAlignment.CENTER_ABSO, new Coord(111, 2), new Coord(12, 16), new ToggleHorizAlign(parent,
                        HorizontalAlignment.LEFT_ABSO)));
        addWindow(new DisplayUnitToggle(new Coord(+00, 3), new Coord(20, 20), VerticalAlignment.TOP_ABSO,
                HorizontalAlignment.CENTER_ABSO, new Coord(129, 2), new Coord(12, 16), new ToggleHorizAlign(parent,
                        HorizontalAlignment.CENTER_ABSO)));
        addWindow(new DisplayUnitToggle(new Coord(+25, 3), new Coord(20, 20), VerticalAlignment.TOP_ABSO,
                HorizontalAlignment.CENTER_ABSO, new Coord(147, 2), new Coord(12, 16), new ToggleHorizAlign(parent,
                        HorizontalAlignment.RIGHT_ABSO)));

        addWindow(new DisplayUnitToggle(new Coord(-25, 28), new Coord(20, 20), VerticalAlignment.TOP_ABSO,
                HorizontalAlignment.CENTER_ABSO, new Coord(111, 23), new Coord(12, 16), new ToggleVertAlign(parent,
                        VerticalAlignment.TOP_ABSO)));
        addWindow(new DisplayUnitToggle(new Coord(+00, 28), new Coord(20, 20), VerticalAlignment.TOP_ABSO,
                HorizontalAlignment.CENTER_ABSO, new Coord(129, 23), new Coord(12, 16), new ToggleVertAlign(parent,
                        VerticalAlignment.CENTER_ABSO)));
        addWindow(new DisplayUnitToggle(new Coord(+25, 28), new Coord(20, 20), VerticalAlignment.TOP_ABSO,
                HorizontalAlignment.CENTER_ABSO, new Coord(147, 23), new Coord(12, 16), new ToggleVertAlign(parent,
                        VerticalAlignment.BOTTOM_ABSO)));
    }

    public static class ToggleVertAlign implements Toggle {
        private DisplayUnitSettable displayToSet;
        private VerticalAlignment alignmentToSet;

        public ToggleVertAlign(DisplayUnitSettable displayToSet, VerticalAlignment alignment) {
            this.displayToSet = displayToSet;
            this.alignmentToSet = alignment;
        }

        @Override
        public void toggle() {
            displayToSet.setVerticalAlignment(alignmentToSet);
            // Reset position to prevent display from becoming lost outside screen
            displayToSet.setOffset(new Coord(0, 0));
        }

        @Override
        public boolean isToggled() {
            return displayToSet.getVerticalAlignment() == alignmentToSet;
        }
    }

    public static class ToggleHorizAlign implements Toggle {
        private DisplayUnitSettable displayToSet;
        private HorizontalAlignment alignmentToSet;

        public ToggleHorizAlign(DisplayUnitSettable displayToSet, HorizontalAlignment alignment) {
            this.displayToSet = displayToSet;
            this.alignmentToSet = alignment;
        }

        @Override
        public void toggle() {
            displayToSet.setHorizontalAlignment(alignmentToSet);
            // Reset position to prevent display from becoming lost outside screen
            displayToSet.setOffset(new Coord(0, 0));
        }

        @Override
        public boolean isToggled() {
            return displayToSet.getHorizontalAlignment() == alignmentToSet;
        }
    }

    @Override
    public String getSubType() {
        return DISPLAY_ID;
    }

    @Override
    public Coord getSize() {
        // TODO: Determine based on position(s) and size(s) of children
        return new Coord(100, 75);
    }

    @Override
    public HorizontalAlignment getHorizontalAlignment() {
        return horizAlign;
    }

    @Override
    public VerticalAlignment getVerticalAlignment() {
        return vertAlign;
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

        DisplayRenderHelper.drawTexture4Quadrants(Tessellator.instance, -10.0f, position, getSize(),
                new Coord(000, 128), new Coord(127, 127));
    }

    @Override
    public ActionResult mouseAction(Coord localMouse, MouseAction action, int... actionData) {
        return super.mouseAction(localMouse, action, actionData);
    }
}
