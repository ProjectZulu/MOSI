package mosi.display.units;

import mosi.DefaultProps;
import mosi.display.DisplayHelper;
import mosi.display.DisplayRenderHelper;
import mosi.display.DisplayUnitFactory;
import mosi.display.hiderules.HideRule.Operator;
import mosi.display.hiderules.HideRules;
import mosi.display.hiderules.HideThresholdRule;
import mosi.display.resource.SimpleImageResource.GuiIconImageResource;
import mosi.display.units.action.ReplaceAction;
import mosi.display.units.windows.DisplayUnitTextBoard;
import mosi.display.units.windows.DisplayUnitTextField;
import mosi.display.units.windows.DisplayUnitToggle;
import mosi.display.units.windows.DisplayWindowMenu;
import mosi.display.units.windows.text.PositionTextValidator;
import mosi.display.units.windows.text.RegularTextValidator;
import mosi.display.units.windows.text.ValidatorBoundedInt;
import mosi.display.units.windows.toggle.ToggleHorizAlign;
import mosi.display.units.windows.toggle.ToggleVertAlign;
import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;

public class DisplayUnitPotion extends DisplayUnitMoveable implements DisplayUnitCountable, DisplayUnitSettable {
    public static final String DISPLAY_ID = "DisplayUnitPotion";
    public static final ResourceLocation inventory = new ResourceLocation("textures/gui/container/inventory.png");
    public static final ResourceLocation countdown = new ResourceLocation(DefaultProps.mosiKey, "countdown.png");

    // User assigned name to item for display. Should only be used for display when necessary and not be null.
    public String nickname = "";
    // Frequency to search player inventory for updated item statistics, most commonly quantity
    public int updateFrequency;
    // For display purposes
    public int textDisplayColor;
    public int trackedPotion; // Id of Potion to be tracked

    public int trackedCount; // Value of tracked property, always duration for Potions
    private int prevTrackedCount;
    public int maxAnalogDuration; // in Ticks

    private transient boolean shouldDisplay;
    private HideRules hidingRules;

    public HideRules getHideRules() {
        return hidingRules;
    }

    public boolean displayAnalogBar;
    public boolean displayNumericCounter;
    public Coord analogOffset;
    public Coord digitalOffset;
    private VerticalAlignment vertAlign = VerticalAlignment.CENTER_ABSO;
    private HorizontalAlignment horizAlign = HorizontalAlignment.CENTER_ABSO;

    public DisplayUnitPotion setEnableDigital(boolean enabled, Coord digitalOffset) {
        this.displayNumericCounter = enabled;
        this.digitalOffset = digitalOffset;
        return this;
    }

    public DisplayUnitPotion disableDigital() {
        this.displayNumericCounter = false;
        return this;
    }

    public DisplayUnitPotion() {
        super(new Coord(0, 0));
        updateFrequency = 20;
        trackedPotion = 1;// Defaults to Speed, choice is arbitrary
        textDisplayColor = 1030655;
        maxAnalogDuration = 60 * 20;
        displayAnalogBar = true;
        displayNumericCounter = false;
        analogOffset = new Coord(1, 18);
        digitalOffset = new Coord(1, 18);
        hidingRules = new HideRules();
        hidingRules.addRule(new HideThresholdRule(0, true, false, Operator.AND));
    }

    public DisplayUnitPotion(int updateFrequency, int trackedPotion) {
        super(new Coord(0, 0));
        this.updateFrequency = updateFrequency;
        this.trackedPotion = trackedPotion;
        this.textDisplayColor = 1030655;
        this.maxAnalogDuration = 60 * 20;
        this.displayAnalogBar = true;
        this.displayNumericCounter = false;
        this.displayAnalogBar = false;
        this.displayNumericCounter = false;
        this.analogOffset = new Coord(1, 18);
        this.digitalOffset = new Coord(1, 18);
        this.hidingRules = new HideRules();
        hidingRules.addRule(new HideThresholdRule(0, true, false, Operator.AND));
    }

    @Override
    public String getType() {
        return DISPLAY_ID;
    }

    @Override
    public Coord getSize() {
        return new Coord(18, 18);
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
            hidingRules = new HideRules();
            hidingRules.addRule(new HideThresholdRule(1, false, false, Operator.AND));

            Potion potion = Potion.potionTypes[trackedPotion];
            mc.thePlayer.isPotionActive(potion);
            PotionEffect effect = mc.thePlayer.getActivePotionEffect(potion);
            trackedCount = effect != null ? effect.getDuration() : 0;
            this.prevTrackedCount = trackedCount;
            hidingRules.update(trackedCount, prevTrackedCount);

            shouldDisplay = Potion.potionTypes[trackedPotion] != null && !hidingRules.shouldHide(trackedCount);
        }
    }

    @Override
    public boolean shouldRender(Minecraft mc) {
        return shouldDisplay;
    }

    @Override
    public void renderDisplay(Minecraft mc, Coord position) {
        mc.renderEngine.bindTexture(countdown);
        if (displayAnalogBar) {
            renderAnalogBar(mc, position, analogOffset, trackedCount, maxAnalogDuration);
        }

        if (displayNumericCounter) {
            renderCounterBar(mc, position, digitalOffset, trackedCount);
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(inventory);
        int iconIndex = Potion.potionTypes[trackedPotion].getStatusIconIndex();
        int iconXCoord = 0 + iconIndex % 8 * 18;
        int iconYCoord = 198 + iconIndex / 8 * 18;
        DisplayRenderHelper.drawTexturedModalRect(Tessellator.instance, -9.0f, position.x, position.z, iconXCoord,
                iconYCoord, 18, 18);
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
        int totalSeconds = counterAmount / 20;

        /* Get Duration in Seconds */
        int seconds = totalSeconds % 60;
        /* Get Duration in Minutes */
        int minutes = (totalSeconds / 60) % 60;
        String formattedTime;
        if (seconds < 10) {
            formattedTime = Integer.toString(minutes);
        } else if (minutes == 0) {
            formattedTime = String.format("%02d", seconds);
        } else {
            formattedTime = minutes + ":" + String.format("%02d", seconds);
        }

        String displayAmount = Integer.toString(counterAmount);
        // 8 is constant chosen by testing to keep the displaystring roughly center. It just works.
        mc.fontRenderer.drawString(formattedTime,
                centerOfDisplay.x + (8 - mc.fontRenderer.getStringWidth(formattedTime) / 2) + offSet.x,
                centerOfDisplay.z + offSet.z, textDisplayColor);
    }

    @Override
    public void mousePosition(Coord localMouse, HoverAction hoverAction, HoverTracker hoverChecker) {
        if (hoverAction == HoverAction.HOVER) {
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
                        private DisplayUnitPotion display;

                        public RegularTextValidator init(DisplayUnitPotion display) {
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

            //TODO Add TextBoard to state what the text field means
            menu.addElement(new DisplayUnitTextBoard(new Coord(-10, 80), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, "Track ID:").setBackgroundImage(null));
            
            menu.addElement(new DisplayUnitTextField(new Coord(23, 80), new Coord(18, 15), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, 2, new ValidatorBoundedInt(0, Potion.potionTypes.length - 1) {

                        private DisplayUnitPotion display;

                        public ValidatorBoundedInt init(DisplayUnitPotion display) {
                            this.display = display;
                            return this;
                        }

                        @Override
                        public boolean isStringValid(String text) {
                            if (!super.isStringValid(text)) {
                                return false;
                            }
                            return Potion.potionTypes[Integer.parseInt(text)] != null;
                        }

                        @Override
                        public void setString(String text) {
                            display.trackedPotion = Integer.parseInt(text);
                        }

                        @Override
                        public String getString() {
                            return Integer.toString(display.trackedPotion);
                        }
                    }.init(this)));
           
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
        return trackedCount;
    }
}
