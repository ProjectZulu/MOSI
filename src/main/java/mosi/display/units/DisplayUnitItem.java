package mosi.display.units;

import mosi.DefaultProps;
import mosi.display.DisplayHelper;
import mosi.display.DisplayRenderHelper;
import mosi.display.DisplayUnitFactory;
import mosi.display.hiderules.HideRules;
import mosi.display.inventoryrules.InventoryRule;
import mosi.display.inventoryrules.InventoryRules;
import mosi.display.inventoryrules.ItemIdMatch;
import mosi.display.inventoryrules.ScrollableInventoryRules;
import mosi.display.resource.SimpleImageResource.GuiIconImageResource;
import mosi.display.units.action.ReplaceAction;
import mosi.display.units.windows.DisplayUnitButton;
import mosi.display.units.windows.DisplayUnitButton.Clicker;
import mosi.display.units.windows.DisplayUnitTextBoard;
import mosi.display.units.windows.DisplayUnitTextField;
import mosi.display.units.windows.DisplayUnitToggle;
import mosi.display.units.windows.DisplayWindowMenu;
import mosi.display.units.windows.DisplayWindowScrollList;
import mosi.display.units.windows.button.CloseClick;
import mosi.display.units.windows.text.PositionTextValidator;
import mosi.display.units.windows.text.RegularTextValidator;
import mosi.display.units.windows.toggle.ToggleHorizAlign;
import mosi.display.units.windows.toggle.ToggleVertAlign;
import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.gson.JsonObject;

public class DisplayUnitItem extends DisplayUnitMoveable implements DisplayUnitCountable, DisplayUnitSettable {
    public static final String DISPLAY_ID = "DisplayUnitItem";
    public static final ResourceLocation countdown = new ResourceLocation(DefaultProps.mosiKey, "countdown.png");

    private boolean displayOnHud;
    // User assigned name to item for display. Should only be used for display when neccessary and not be null.
    public String nickname;
    // Frequency to search player inventory for updated item statistics, most commonly quantity
    private int updateFrequency = 20;

    // For display purposes
    private int textDisplayColor = 1030655;
    // Display ItemStack used when counting rules do not find an ItemStack
    private ItemStack missingDisplayStack;
    // Matching rules for Counting
    private InventoryRules countingRules;
    private HideRules hidingRules;
    private TrackMode trackMode;

    boolean displayAnalogBar = true;
    boolean displayNumericCounter = true;

    // Information required to display
    private DisplayStats displayStats;
    private DisplayStats prevDisplayStat;

    private Coord analogOffset = new Coord(16, 13);
    private Coord digitalOffset = new Coord(16, -4);
    private VerticalAlignment vertAlign = VerticalAlignment.CENTER_ABSO;
    private HorizontalAlignment horizAlign = HorizontalAlignment.CENTER_ABSO;

    public DisplayUnitItem() {
        super(new Coord(0, 0));
        displayOnHud = true;
        nickname = "";
        trackMode = TrackMode.QUANTITY;
        countingRules = new InventoryRules();
        countingRules.addRule(new ItemIdMatch("grass", true));
        hidingRules = new HideRules();
        // hidingRules.addRule(new HideUnchangedRule(30, false, Operator.AND));
        // hidingRules.addRule(new HideThresholdRule(10, true, false, Operator.AND));
        missingDisplayStack = new ItemStack(Blocks.dirt);
    }

    /* Changes the quality that is being counted */
    public enum TrackMode {
        DURABILITY, QUANTITY, DURATION; // Duration not needed as PotionDisplayUnit will need to be seperate?
    }

    public static class DisplayStats {
        public final ItemStack stackToDisplay;
        public final int trackedCount;
        public final int maximumCount;

        public DisplayStats(ItemStack stackToDisplay, int trackedCount, int maximumCount) {
            this.stackToDisplay = stackToDisplay.copy();
            this.trackedCount = trackedCount;
            this.maximumCount = maximumCount;
        }
    }

    @Override
    public String getType() {
        return DISPLAY_ID;
    }

    @Override
    public Coord setOffset(Coord offset) {
        return this.offset = offset;
    }

    @Override
    public Coord getSize() {
        return new Coord(largestXDistance(getOffset().x, analogOffset.x, digitalOffset.x), largestZDistance(
                getOffset().z, analogOffset.z, digitalOffset.z));
    }

    private int largestXDistance(int iconCoord, int anaOffset, int digOffset) {
        // icon is 16x16 and its base is origin (0,0) for analog and digital offsets
        int farEdgePointAnalog = anaOffset >= 0 ? anaOffset + 16 : anaOffset;
        int farEdgePointDigit = digOffset >= 0 ? digOffset + 8 : digOffset;
        if (farEdgePointAnalog >= 0 && farEdgePointDigit >= 0) {
            return Math.max(Math.max(farEdgePointAnalog, farEdgePointDigit), 16);
        } else if (farEdgePointAnalog >= 0) {
            return Math.max(Math.max(farEdgePointAnalog, farEdgePointAnalog - farEdgePointDigit),
                    16 - farEdgePointDigit);
        } else if (farEdgePointDigit >= 0) {
            return Math.max(Math.max(farEdgePointDigit, farEdgePointDigit - farEdgePointAnalog),
                    16 - farEdgePointAnalog);
        } else {
            // Else Case both are < 0
            return Math.max(16 - farEdgePointAnalog, 16 - farEdgePointDigit);
        }
    }

    private int largestZDistance(int iconCoord, int anaOffset, int digOffset) {
        // icon is 16x16 and its base is origin (0,0) for analog and digital offsets
        int farEdgePointAnalog = anaOffset >= 0 ? anaOffset + 4 : anaOffset;
        int farEdgePointDigit = digOffset >= 0 ? digOffset + 8 : digOffset;
        if (farEdgePointAnalog >= 0 && farEdgePointDigit >= 0) {
            return Math.max(Math.max(farEdgePointAnalog, farEdgePointDigit), 16);
        } else if (farEdgePointAnalog >= 0) {
            return Math.max(Math.max(farEdgePointAnalog, farEdgePointAnalog - farEdgePointDigit),
                    16 - farEdgePointDigit);
        } else if (farEdgePointDigit >= 0) {
            return Math.max(Math.max(farEdgePointDigit, farEdgePointDigit - farEdgePointAnalog),
                    16 - farEdgePointAnalog);
        } else {
            // Else Case both are < 0
            return Math.max(16 - farEdgePointAnalog, 16 - farEdgePointDigit);
        }
    }

    @Override
    public VerticalAlignment getVerticalAlignment() {
        return vertAlign;
    }

    @Override
    public VerticalAlignment setVerticalAlignment(VerticalAlignment alignment) {
        return vertAlign = alignment;
    }

    @Override
    public HorizontalAlignment getHorizontalAlignment() {
        return horizAlign;
    }

    @Override
    public HorizontalAlignment setHorizontalAlignment(HorizontalAlignment alignment) {
        return horizAlign = alignment;
    }

    @Override
    public void onUpdate(Minecraft mc, int ticks) {
        if (ticks % updateFrequency == 0) {
            prevDisplayStat = displayStats;
            displayStats = calculateDisplayStats(mc);
            Integer count = displayStats != null ? displayStats.trackedCount : null;
            Integer prevCount = prevDisplayStat != null ? prevDisplayStat.trackedCount : null;
            hidingRules.update(count, prevCount);
            if (displayStats != null) {
                displayOnHud = !hidingRules.shouldHide(count);
            } else {
                displayOnHud = false;
            }
        }
    }

    private DisplayStats calculateDisplayStats(Minecraft mc) {
        ItemStack stackToDisplay = missingDisplayStack;
        int trackedCount = 0;
        boolean foundMatch = false;
        RULE_LOOP: for (InventoryRule rule : countingRules) {
            ItemStack[] inventory = mc.thePlayer.inventory.mainInventory;
            for (int i = 0; i < inventory.length; i++) {
                if (inventory[i] == null) {
                    continue;
                }
                ItemStack itemStack = inventory[i];
                if (rule.isMatch(itemStack, i, false, mc.thePlayer.inventory.currentItem == i)) {
                    if (!foundMatch) {
                        stackToDisplay = itemStack.copy();
                        foundMatch = true;
                    }
                    trackedCount += countStack(itemStack);
                    if (!rule.allowMultipleMatches()) {
                        continue RULE_LOOP;
                    }
                }
            }

            inventory = mc.thePlayer.inventory.armorInventory;
            for (int i = 0; i < inventory.length; i++) {
                if (inventory[i] == null) {
                    continue;
                }
                ItemStack itemStack = inventory[i];
                if (rule.isMatch(itemStack, i, true, mc.thePlayer.inventory.currentItem == i)) {
                    if (!foundMatch) {
                        stackToDisplay = itemStack.copy();
                        foundMatch = true;
                    }
                    trackedCount += countStack(itemStack);
                    if (!rule.allowMultipleMatches()) {
                        continue RULE_LOOP;
                    }
                }
            }
        }

        int maximumCount;
        if (trackMode == TrackMode.DURABILITY) {
            maximumCount = stackToDisplay.getMaxDamage();
        } else {
            maximumCount = 64;
        }
        return new DisplayStats(stackToDisplay, trackedCount, maximumCount);
    }

    @Override
    public boolean shouldRender(Minecraft mc) {
        return displayOnHud;
    }

    @Override
    public void renderDisplay(Minecraft mc, Coord position) {
        DisplayStats displayStats = getDisplayInfo(mc);
        if (displayStats == null) {
            return;
        }
        if (displayAnalogBar) {
            renderAnalogBar(mc, position, analogOffset, displayStats.trackedCount, displayStats.maximumCount);
        }
        GL11.glDisable(GL11.GL_BLEND);
        if (displayNumericCounter) {
            renderCounterBar(mc, position, digitalOffset, displayStats.trackedCount);
        }

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        float opacity = 1;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, opacity);
        RenderItem renderItem = new RenderItem();
        renderItem.zLevel = 200.0F;
        renderItem.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, displayStats.stackToDisplay,
                position.x, position.z);
        GL11.glDisable(GL11.GL_BLEND);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    public DisplayStats getDisplayInfo(Minecraft mc) {
        if (displayStats == null) {
            displayStats = calculateDisplayStats(mc);
        }
        return displayStats;
    }

    private int countStack(ItemStack stackToCount) {
        if (trackMode == TrackMode.DURABILITY) {
            int currentDamage = stackToCount.getItemDamage();
            int maxDamage = stackToCount.getItem().getMaxDamage();
            return maxDamage - currentDamage;
        } else {
            return stackToCount.stackSize;
        }
    }

    /**
     * Used to Draw Analog Bar.
     * 
     * @param mc The Minecraft Instance
     * @param centerOfDisplay The Center Position where the bar needs to be offset From.
     * @param analogValue The value representing how full the Bar is
     * @param analogMax The value that represents the width of the full bar.
     */
    protected void renderAnalogBar(Minecraft mc, Coord centerOfDisplay, Coord offSet, int analogValue, int analogMax) {
        mc.renderEngine.bindTexture(countdown);
        int scaledValue = scaleAnalogizeValue(analogValue, analogMax);
        DisplayRenderHelper.drawTexturedModalRect(Tessellator.instance, 10.0f, centerOfDisplay.x + offSet.x,
                centerOfDisplay.z + offSet.z, 0, 0, 16, 3);
        if (scaledValue > 9) {
            DisplayRenderHelper.drawTexturedModalRect(Tessellator.instance, 10.0f, centerOfDisplay.x + offSet.x,
                    centerOfDisplay.z + offSet.z, 0, 3, scaledValue, 3);
        } else if (scaledValue > 4) {
            DisplayRenderHelper.drawTexturedModalRect(Tessellator.instance, 10.0f, centerOfDisplay.x + offSet.x,
                    centerOfDisplay.z + offSet.z, 0, 6, scaledValue, 3);
        } else {
            DisplayRenderHelper.drawTexturedModalRect(Tessellator.instance, 10.0f, centerOfDisplay.x + offSet.x,
                    centerOfDisplay.z + offSet.z, 0, 9, scaledValue, 3);
        }
    }

    /**
     * Scale a tracked value from range [0-analogMax] to fit the display bars resolution of [0-16]
     */
    private int scaleAnalogizeValue(int analogValue, int analogMax) {
        if (analogValue > analogMax) {
            analogValue = analogMax;
        }
        if (analogValue < 0) {
            analogValue = 0;
        }
        return (int) ((float) (analogValue) / (float) (analogMax) * 18);
    }

    /**
     * Used to Draw Analog Bar.
     * 
     * @param mc The Minecraft Instance
     * @param fontRenderer The fontRenderer
     * @param centerOfDisplay The Center Position where the bar is offset From.
     * @param analogValue The value representing how full the Bar is
     * @param analogMax The value that represents the width of the full bar.
     */
    protected void renderCounterBar(Minecraft mc, Coord centerOfDisplay, Coord offSet, int counterAmount) {
        String displayAmount = Integer.toString(counterAmount);
        switch (getHorizontalAlignment()) {
        case CENTER_ABSO:
            mc.fontRenderer.drawString(displayAmount,
                    centerOfDisplay.x + 8 - mc.fontRenderer.getStringWidth(displayAmount) / 2 + offSet.x,
                    centerOfDisplay.z - offSet.z, textDisplayColor);
            break;
        case LEFT_ABSO:
            mc.fontRenderer.drawString(displayAmount, centerOfDisplay.x + offSet.x, centerOfDisplay.z - offSet.z,
                    textDisplayColor);
            break;
        case RIGHT_ABSO:
            mc.fontRenderer.drawString(displayAmount, centerOfDisplay.x - mc.fontRenderer.getStringWidth(displayAmount)
                    + offSet.x, centerOfDisplay.z - offSet.z, textDisplayColor);
            break;
        }
    }

    /**
     * Helper method that Maps the real value provided (representing damage typically) to a different scale (typically
     * resolution, 16)
     * 
     * @param realValue represents value in Set 1
     * @param realMax is the max value in set 1, min value is assumed zero.
     * @param scaleMax is the max value in set 2, min value is assumed zero.
     * @return realValue in set 2
     */
    protected int mapValueToScale(int realValue, int realMax, int scaleMax) {
        return realValue > realMax ? scaleMax : realValue < 0 ? 0 : (int) (((float) realValue) / realMax * scaleMax);
    }

    @Override
    public void mousePosition(Coord localMouse, HoverAction hoverAction, HoverTracker hoverChecker) {
        if(hoverAction == HoverAction.HOVER) {
            hoverChecker.markHoverFound();
        }
    }

    @Override
    public ActionResult mouseAction(Coord localMouse, MouseAction action, int... actionData) {
        if (action == MouseAction.CLICK && actionData[0] == 1 && DisplayHelper.isCursorOverDisplay(localMouse, this)) {
            DisplayWindowMenu menu = new DisplayWindowMenu(getOffset(), getHorizontalAlignment(),
                    getVerticalAlignment());

            menu.addElement(new DisplayUnitTextField(new Coord(0, 4), new Coord(80, 15), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, 13, new RegularTextValidator() {
                        private DisplayUnitItem display;

                        public RegularTextValidator init(DisplayUnitItem display) {
                            this.display = display;
                            return this;
                        }

                        @Override
                        public void setString(String text) {
                            display.nickname = text;
                        }

                        @Override
                        public String getString() {
                            return display.nickname;
                        }
                    }.init(this)));

            menu.addElement(new DisplayUnitTextField(new Coord(-17, 19), new Coord(32, 15), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, 5, new PositionTextValidator(this, true)));
            menu.addElement(new DisplayUnitTextField(new Coord(+18, 19), new Coord(32, 15), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, 5, new PositionTextValidator(this, false)));

            menu.addElement(new DisplayUnitToggle(new Coord(-22, 34), new Coord(20, 20), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, new ToggleHorizAlign(this, HorizontalAlignment.LEFT_ABSO))
                    .setIconImageResource(new GuiIconImageResource(new Coord(111, 2), new Coord(12, 16))));
            menu.addElement(new DisplayUnitToggle(new Coord(+00, 34), new Coord(20, 20), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, new ToggleHorizAlign(this, HorizontalAlignment.CENTER_ABSO))
                    .setIconImageResource(new GuiIconImageResource(new Coord(129, 2), new Coord(12, 16))));

            menu.addElement(new DisplayUnitToggle(new Coord(+22, 34), new Coord(20, 20), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, new ToggleHorizAlign(this, HorizontalAlignment.RIGHT_ABSO))
                    .setIconImageResource(new GuiIconImageResource(new Coord(147, 2), new Coord(12, 16))));
            menu.addElement(new DisplayUnitToggle(new Coord(-22, 55), new Coord(20, 20), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, new ToggleVertAlign(this, VerticalAlignment.TOP_ABSO))
                    .setIconImageResource(new GuiIconImageResource(new Coord(111, 23), new Coord(12, 16))));

            menu.addElement(new DisplayUnitToggle(new Coord(+00, 55), new Coord(20, 20), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, new ToggleVertAlign(this, VerticalAlignment.CENTER_ABSO))
                    .setIconImageResource(new GuiIconImageResource(new Coord(129, 23), new Coord(12, 16))));
            menu.addElement(new DisplayUnitToggle(new Coord(+22, 55), new Coord(20, 20), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, new ToggleVertAlign(this, VerticalAlignment.BOTTOM_ABSO))
                    .setIconImageResource(new GuiIconImageResource(new Coord(147, 23), new Coord(12, 16))));

            menu.addElement(new DisplayUnitButton(new Coord(0, 77), new Coord(80, 20), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, new Clicker() {
                        private InventoryRules rules;
                        private VerticalAlignment parentVert;
                        private HorizontalAlignment parentHorz;

                        private Clicker init(InventoryRules rules, VerticalAlignment parentVert,
                                HorizontalAlignment parentHorz) {
                            this.rules = rules;
                            this.parentVert = parentVert;
                            this.parentHorz = parentHorz;
                            return this;
                        }

                        @Override
                        public ActionResult onClick() {
                            return ActionResult.SIMPLEACTION;
                        }

                        @Override
                        public ActionResult onRelease() {
                            return new ReplaceAction(new DisplayWindowScrollList(new Coord(0, 0), new Coord(140, 200),
                                    25, parentVert, parentHorz, new ScrollableInventoryRules(rules)), true);
                        }
                    }.init(countingRules, getVerticalAlignment(), getHorizontalAlignment()), "Count Rules"));

            menu.addElement(new DisplayUnitButton(new Coord(0, 98), new Coord(80, 20), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, new Clicker() {
                        private HideRules rules;
                        private VerticalAlignment parentVert;
                        private HorizontalAlignment parentHorz;

                        private Clicker init(HideRules rules, VerticalAlignment parentVert,
                                HorizontalAlignment parentHorz) {
                            this.rules = rules;
                            this.parentVert = parentVert;
                            this.parentHorz = parentHorz;
                            return this;
                        }

                        @Override
                        public ActionResult onClick() {
                            return ActionResult.SIMPLEACTION;
                        }

                        @Override
                        public ActionResult onRelease() {
                            DisplayWindowMenu menu = new DisplayWindowMenu(new Coord(0, 0), parentHorz, parentVert)
                                    .forceSize(new Coord(245, 85)).setBackgroundImage(null);
                            menu.addElement(new DisplayUnitTextBoard(new Coord(0, -28), VerticalAlignment.BOTTOM_ABSO,
                                    HorizontalAlignment.CENTER_ABSO,
                                    "Hide Rules are not supported via GUI at this time.",
                                    "You may use the GSON configuration files for editing this property.",
                                    "Hopefully someday Soon."));
                            menu.addElement(new DisplayUnitButton(new Coord(0, -2), new Coord(60, 25),
                                    VerticalAlignment.BOTTOM_ABSO, HorizontalAlignment.CENTER_ABSO,
                                    new CloseClick(menu), "Close"));
                            return new ReplaceAction(menu, true);
                        }
                    }.init(hidingRules, getVerticalAlignment(), getHorizontalAlignment()), "Hide Rules"));

            return new ReplaceAction(menu, true);
        }

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
    public int getCount() {
        return displayStats != null ? displayStats.trackedCount : 0;
    }
}
