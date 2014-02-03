package mosi.display.units.windows;

import mosi.display.DisplayHelper;
import mosi.display.DisplayRenderHelper;
import mosi.display.units.DisplayUnit;
import mosi.display.units.DisplayUnit.ActionResult;
import mosi.display.units.DisplayUnit.ActionResult.SimpleAction;
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

import com.sun.xml.internal.ws.message.StringHeader;

/**
 * Simple menu implementation. It is essentially list of buttons/interactables
 */
public class DisplayWindowMenu extends DisplayWindow {
    public static final String DISPLAY_ID = "DisplayWindowMenu";
    private static final ResourceLocation widgets = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation guiButton = new ResourceLocation("mosi", "buttongui.png");

    private HorizontalAlignment horizAlign;
    private VerticalAlignment vertAlign;
    private Coord size;

    public DisplayWindowMenu(Coord coord, HorizontalAlignment horizAlign, VerticalAlignment vertAlign) {
        super(coord);
        this.horizAlign = horizAlign;
        this.vertAlign = vertAlign;
        this.size = new Coord(20, 20);
    }

    public static class PositionTextValidator implements Validator {
        private DisplayUnitSettable display;
        private boolean xCoord;

        public PositionTextValidator(DisplayUnitSettable settableDisplay, boolean xCoord) {
            this.display = settableDisplay;
            this.xCoord = xCoord;
        }

        @Override
        public boolean isCharacterValid(char eventCharacter) {
            return ('-' == eventCharacter || Character.isDigit(eventCharacter))
                    && ChatAllowedCharacters.isAllowedCharacter(eventCharacter);
        }

        @Override
        public boolean isStringValid(String text) {
            return StringHelper.isInteger(text);
        }

        @Override
        public void setString(String text) {
            if (xCoord) {
                display.setOffset(new Coord(Integer.parseInt(text), display.getOffset().z));
            } else {
                display.setOffset(new Coord(display.getOffset().x, Integer.parseInt(text)));
            }
        }

        @Override
        public String getString() {
            if (xCoord) {
                return Integer.toString(display.getOffset().x);
            } else {
                return Integer.toString(display.getOffset().z);
            }
        }
    }

    @Override
    public boolean addWindow(DisplayUnit window) {
        if (super.addWindow(window)) {
            int minX = 0;
            int maxX = 0;
            int minY = 0;
            int maxY = 0;
            for (DisplayUnit display : children) {
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
    public boolean removeWindow(DisplayUnit window) {
        if (super.removeWindow(window)) {
            int minX = 0;
            int maxX = 0;
            int minY = 0;
            int maxY = 0;
            for (DisplayUnit display : children) {
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
