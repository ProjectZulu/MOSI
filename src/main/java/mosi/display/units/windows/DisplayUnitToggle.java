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

import mosi.display.DisplayHelper;
import mosi.display.DisplayRenderHelper;
import mosi.display.DisplayUnitFactory;
import mosi.display.units.DisplayUnit;
import mosi.display.units.DisplayUnitItem;
import mosi.display.units.DisplayUnit.ActionResult;
import mosi.display.units.DisplayUnit.HorizontalAlignment;
import mosi.display.units.DisplayUnit.MouseAction;
import mosi.display.units.DisplayUnit.VerticalAlignment;
import mosi.display.units.DisplayUnit.ActionResult.SimpleAction;
import mosi.utilities.Coord;

public class DisplayUnitToggle implements DisplayUnit {
    public static final ResourceLocation guiButton = new ResourceLocation("mosi", "buttongui.png");
    public static final ResourceLocation guiIcons = new ResourceLocation("mosi", "icons.png");

    public static final String DISPLAY_ID = "DisplayUnitToggle";

    private Coord offset;
    private Coord size;
    private boolean isClicked;
    private boolean isMouseOver;

    private Optional<String> displayText;
    private Optional<Coord> iconCoord;
    private Coord iconSize;

    private VerticalAlignment vertAlign;
    private HorizontalAlignment horizAlign;
    private Toggle toggle;

    public static interface Toggle {
        /* Toggles the internal value */
        public abstract void toggle();

        /* Checks if the value is toggled, used for rendering */
        public abstract boolean isToggled();
    }

    public DisplayUnitToggle(Coord offset, Coord size, VerticalAlignment vertAlign, HorizontalAlignment horzAlign,
            String displayText, Toggle toggle) {
        this.offset = offset;
        this.size = size;
        this.vertAlign = vertAlign;
        this.horizAlign = horzAlign;
        this.displayText = Optional.of(displayText);
        this.toggle = toggle;
        this.iconCoord = Optional.absent();
    }
    
    // Toggle toggleLeftAligned = new Toggle() {
    // private DisplayUnitItem displayToToggle;
    //
    // private Toggle init(DisplayUnitItem displayToToggle) {
    // this.displayToToggle = displayToToggle;
    // return this;
    // }
    //
    // @Override
    // public void toggle() {
    // // displayToToggle.setHorizontalAlignment(HorizontalAlignment.LEFT_ABSO);
    // }
    //
    // @Override
    // public boolean isToggles() {
    // return displayToToggle.getHorizontalAlignment() == HorizontalAlignment.LEFT_ABSO;
    // }
    //
    // }.init(displayItem);

    public DisplayUnitToggle(Coord offset, Coord size, VerticalAlignment vertAlign, HorizontalAlignment horzAlign,
            Coord iconCoord, Coord iconSize, Toggle toggle) {
        this.offset = offset;
        this.size = size;
        this.vertAlign = vertAlign;
        this.horizAlign = horzAlign;
        this.iconCoord = Optional.of(iconCoord);
        this.iconSize = iconSize;
        this.toggle = toggle;
        this.displayText = Optional.absent();
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
        return horizAlign;
    }

    @Override
    public VerticalAlignment getVerticalAlignment() {
        return vertAlign;
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

        // TODO: GuiIcons should be a passable parameter
        mc.getTextureManager().bindTexture(guiIcons);
        /* GUI Image */
        if (iconCoord.isPresent()) {
            DisplayRenderHelper.drawTexturedModalRect(Tessellator.instance, 1.0f, position.x + getSize().x / 2
                    - iconSize.x / 2, position.z + getSize().z / 2 - iconSize.z / 2, iconCoord.get().x,
                    iconCoord.get().z, iconSize.x, iconSize.z);
        }

        // DisplayRenderHelper.drawTexturedModalRect(Tessellator.instance, 1.0f, position.x + 3, position.z + 2, 111, 2,
        // 12, 16);
        mc.getTextureManager().bindTexture(guiButton);

        /* Background */
        // TODO: The Background Texture and Coords for Toggled/UnToggled/Hover need to be configurable via a setter, BUT
        // the default is set during the constructor
        if (toggle.isToggled()) {
            DisplayRenderHelper.drawTexture4Quadrants(Tessellator.instance, -1.0f, position, getSize(), new Coord(129,
                    129), new Coord(127, 127));
        } else if (isMouseOver) {
            DisplayRenderHelper.drawTexture4Quadrants(Tessellator.instance, -1.0f, position, getSize(), new Coord(000,
                    0), new Coord(127, 127));
        } else {
            DisplayRenderHelper.drawTexture4Quadrants(Tessellator.instance, -0.1f, position, getSize(), new Coord(129,
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
        return new SimpleAction(isMouseOver);
    }

    @Override
    public ActionResult mouseAction(Coord localMouse, MouseAction action, int... actionData) {
        if (action == MouseAction.CLICK && actionData[0] == 0 && DisplayHelper.isCursorOverDisplay(localMouse, this)
                && !toggle.isToggled()) {
            toggle.toggle();
            return new SimpleAction(true);
        } else {
            return ActionResult.NOACTION;
        }
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
