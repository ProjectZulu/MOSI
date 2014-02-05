package mosi.display.units;

import mosi.display.DisplayHelper;
import mosi.display.DisplayRenderHelper;
import mosi.display.DisplayUnitFactory;
import mosi.display.inventoryrules.ItemHandMatch;
import mosi.display.inventoryrules.ItemIdMatch;
import mosi.display.inventoryrules.ItemMetaMatch;
import mosi.display.inventoryrules.ItemSlotMatch;
import mosi.display.units.DisplayUnit.ActionResult.SimpleAction;
import mosi.display.units.windows.DisplayWindow;
import mosi.display.units.windows.DisplayWindowScrollList.Scrollable;
import mosi.display.units.windows.DisplayWindowScrollList.ScrollableElement;
import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;

public class DisplayUnitInventoryRule extends DisplayWindow implements ScrollableElement {
    public static final String DISPLAY_ID = "DisplayWindowMenu";
    private static final ResourceLocation guiButton = new ResourceLocation("mosi", "buttongui.png");

    private RULEID ruleId;
    private Coord size;
    private VerticalAlignment vertAlign = VerticalAlignment.TOP_ABSO;
    private HorizontalAlignment horizAlign = HorizontalAlignment.LEFT_ABSO;
    private boolean isMouseOver;
    private boolean scrollVisibility;
    private Scrollable container;

    // TODO: A more generic way to get editable segments of InventoryRules. Theres only a few atm, so individual support
    // is manageable
    public enum RULEID {
        HAND, ID, IDMETA, SLOT;
    }

    /**
     * 
     * @param inventoryRule
     * @param Scrollable is used for this unit to remove itself, if possible
     */
    public DisplayUnitInventoryRule(ItemHandMatch inventoryRule, Scrollable container) {
        ruleId = RULEID.HAND;
        size = new Coord(40, 20);
        this.container = container;
    }

    public DisplayUnitInventoryRule(ItemIdMatch inventoryRule, Scrollable container) {
        ruleId = RULEID.ID;

        // Add TextBox to set string id --> will eventually be scroll list
        // Add Toggle to set multipleMatches
        size = new Coord(40, 20);
        this.container = container;
    }

    public DisplayUnitInventoryRule(ItemMetaMatch inventoryRule, Scrollable container) {
        ruleId = RULEID.IDMETA;
        // Add TextBox to set string id --> will eventually be scroll list
        // Add TextBox to set string damage (--> eventually scroll list that selected id + damage?)
        // Add Toggle to set multipleMatches
        size = new Coord(40, 20);
        this.container = container;
    }

    public DisplayUnitInventoryRule(ItemSlotMatch inventoryRule, Scrollable container) {
        ruleId = RULEID.SLOT;
        // Add TextBox to set string slotId --> will eventually be scroll list
        // Add TextBox to set string armorSlot (--> eventually scroll list that selected id + damage?)
        // Add Toggle to set multipleMatches
        size = new Coord(40, 20);
        this.container = container;
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
    public Coord setOffset(Coord offset) {
        return this.offset = offset;
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
    public VerticalAlignment setVerticalAlignment(VerticalAlignment alignment) {
        return vertAlign = alignment;
    }

    @Override
    public HorizontalAlignment setHorizontalAlignment(HorizontalAlignment alignment) {
        return horizAlign = alignment;
    }

    @Override
    public void onUpdate(Minecraft mc, int ticks) {
        super.onUpdate(mc, ticks);
    }

    @Override
    public boolean shouldRender(Minecraft mc) {
        return true;
    }

    @Override
    public void renderSubDisplay(Minecraft mc, Coord position) {
        FontRenderer fontrenderer = mc.fontRenderer;
        mc.getTextureManager().bindTexture(guiButton);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.func_148821_a(770, 771, 1, 0);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        if (isMouseOver) {
            DisplayRenderHelper.drawTexture4Quadrants(Tessellator.instance, -1.0f, position, getSize(), new Coord(000,
                    000), new Coord(127, 127));
        } else {
            DisplayRenderHelper.drawTexture4Quadrants(Tessellator.instance, -5.0f, position, getSize(), new Coord(000,
                    128), new Coord(127, 127));
        }
    }

    @Override
    public SimpleAction mousePosition(Coord localMouse) {
        isMouseOver = DisplayHelper.isCursorOverDisplay(localMouse, this);
        return isMouseOver ? ActionResult.SIMPLEACTION : ActionResult.NOACTION;
    }

    @Override
    public ActionResult mouseAction(Coord localMouse, MouseAction action, int... actionData) {
        return super.mouseAction(localMouse, action, actionData);
    }

    @Override
    public ActionResult keyTyped(char eventCharacter, int eventKey) {
        return super.keyTyped(eventCharacter, eventKey);
    }

    @Override
    public JsonObject saveCustomData(JsonObject jsonObject) {
        return null;
    }

    @Override
    public void loadCustomData(DisplayUnitFactory factory, JsonObject customData) {
    }

    @Override
    public void setScrollVisibity(boolean visibility) {
        scrollVisibility = visibility;
    }

    @Override
    public boolean isVisibleInScroll() {
        return scrollVisibility;
    }
}
