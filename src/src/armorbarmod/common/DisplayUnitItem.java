package armorbarmod.common;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.Point;

public abstract class DisplayUnitItem extends DisplayUnit {

    RenderItem renderItem = new RenderItem();

    protected static final ResourceLocation countdown = new ResourceLocation(DefaultProps.armorBarKey,
            "countdown.png");
    
    public DisplayUnitItem(String name, boolean shouldDisplay, int displayColor, Point displayOffset) {
        super(name, shouldDisplay, displayColor, displayOffset);
    }

    /**
     * Method used to draw GUI, called from Child renderDisplay after Specific information has been processed
     * 
     * @param mc The Minecraft Instance
     * @param itemStackToRender The ItemStack of the Item we are wanting To Render
     * @param textureLocation TextureLocation for the GUI elements we want to draw
     * @param analogAmount The scaled value for the analog counter
     * @param counterAmount The scaled value for the digital counter
     */
    protected void renderSpecifics(Minecraft mc, ItemStack itemStackToRender, Icon textureLocation, int analogAmount,
            int counterAmount) {
        ScaledResolution scaledResolition = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        Point centerOfDisplay = new Point(scaledResolition.getScaledWidth() / 2 + displayOffset.getX(),
                scaledResolition.getScaledHeight() - displayOffset.getY());

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, opacity);
//        GL11.glEnable(GL11.GL_POLYGON_STIPPLE);
        renderItem.zLevel = 200.0F;
        renderItem.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, itemStackToRender, centerOfDisplay.getX(),
                centerOfDisplay.getY());
        GL11.glDisable(GL11.GL_BLEND);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        if (displayAnalogBar) {
            renderAnalogBar(mc, centerOfDisplay, analogAmount, 16);
        }

        if (displayNumericCounter) {
            renderCounterBar(mc, centerOfDisplay, counterAmount);
        }

        GL11.glPopMatrix();
    }

    /**
     * Used to Draw Analog Bar.
     * 
     * @param mc The Minecraft Instance
     * @param centerOfDisplay The Center Position where the bar needs to be offset From.
     * @param analogValue The value representing how full the Bar is
     * @param analogMax The value that represents the width of the full bar.
     */
    protected void renderAnalogBar(Minecraft mc, Point centerOfDisplay, int analogValue, int analogMax) {
        mc.renderEngine.bindTexture(countdown);
        drawTexturedModalRect(centerOfDisplay.getX(), centerOfDisplay.getY() + 16, 0, 0, analogMax, 3);
        if (analogValue > 9) {
            drawTexturedModalRect(centerOfDisplay.getX() + displayAnalogOffset.getX(), centerOfDisplay.getY()
                    + displayAnalogOffset.getY(), 0, 3, analogValue, 3);
        } else if (analogValue > 4) {
            drawTexturedModalRect(centerOfDisplay.getX() + displayAnalogOffset.getX(), centerOfDisplay.getY()
                    + displayAnalogOffset.getY(), 0, 6, analogValue, 3);
        } else {
            drawTexturedModalRect(centerOfDisplay.getX() + displayAnalogOffset.getX(), centerOfDisplay.getY()
                    + displayAnalogOffset.getY(), 0, 9, analogValue, 3);
        }
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
    protected void renderCounterBar(Minecraft mc, Point centerOfDisplay, int counterAmount) {

        String displayAmount = Integer.toString(counterAmount);
        mc.fontRenderer.drawString(
                displayAmount,
                centerOfDisplay.getX() + 8 - mc.fontRenderer.getStringWidth(displayAmount) / 2
                        + displayCounterOffset.getX(), centerOfDisplay.getY() - displayCounterOffset.getY(),
                displayColor);
    }

    /**
     * Helper Method to Convert from the Sprite Coordinate Index to the Pixel(X,Y) on the Spritesheet where the desired
     * Sprite is. Resolution is assumed 16x16 squares.
     * 
     * @param iconIndex
     * @return Resolution Coordinates
     */
    protected Point getIconCoordFromIndex(int iconIndex) {
        Point point = new Point();
        int tempIndex = iconIndex;
        while (tempIndex > (32 - 1)) {
            tempIndex -= (32 - 1);
            point.setY(point.getY() + 1);
        }

        /*
         * Note that it is scaled by 16 to represent resolution distance that it should point to (16 pixels being the
         * width of each icon)
         */
        point.setX(tempIndex * 16);
        point.setY(point.getY() * 16);
        return point;
    }

}
