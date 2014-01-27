package mosi.display.units;

import mosi.DefaultProps;
import mosi.display.DisplayRenderHelper;
import mosi.display.DisplayUnitFactory;
import mosi.display.hiderules.HideRule;
import mosi.display.hiderules.HideRule.Operator;
import mosi.display.hiderules.HideRules;
import mosi.display.hiderules.HideThresholdRule;
import mosi.display.units.DisplayUnit.MouseAction;
import mosi.display.units.DisplayUnit.ActionResult.NoAction;
import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Optional;
import com.google.gson.JsonObject;

public class DisplayUnitPotion implements DisplayUnitCountable {
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
    public Coord getOffset() {
        return new Coord(0, 0);
    }

    @Override
    public Coord getSize() {
        return new Coord(18, 18);
    }

    @Override
    public VerticalAlignment getVerticalAlignment() {
        return VerticalAlignment.CENTER_ABSO;
    }

    @Override
    public HorizontalAlignment getHorizontalAlignment() {
        return HorizontalAlignment.CENTER_ABSO;
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
                centerOfDisplay.z + offSet.z, 0, 0, analogMax, 3);
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
    public void mouseMove(int mouseLocalX, int mouseLocalY) {
    }

    @Override
    public ActionResult mouseAction(Coord localMouse, MouseAction action, int... actionData) {
        return new NoAction();
    }

    @Override
    public ActionResult keyTyped(char eventCharacter, int eventKey) {
        return new NoAction();
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
