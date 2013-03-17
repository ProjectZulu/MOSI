package armorbarmod.common;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureStitched;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class DisplayUnitItem extends DisplayUnit{

	public DisplayUnitItem(String name, boolean shouldDisplay, int displayColor, Point displayOffset) {
		super(name, shouldDisplay, displayColor, displayOffset);
	}
	
	/**
	 * Method used to draw GUI, called from Child renderDisplay after Specific information has been processed
	 * @param mc The Minecraft Instance
	 * @param itemStackToRender The ItemStack of the Item we are wanting To Render
	 * @param textureLocation TextureLocation for the GUI elements we want to draw
	 * @param analogAmount	The scaled value for the analog counter
	 * @param counterAmount The scaled value for the digital counter
	 */
	protected void renderSpecifics(Minecraft mc, ItemStack itemStackToRender, Icon textureLocation, int analogAmount, int counterAmount) {
		ScaledResolution scaledResolition = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		Point centerOfDisplay = new Point(scaledResolition.getScaledWidth()/2+displayOffset.getX(), scaledResolition.getScaledHeight()-displayOffset.getY());
		
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, opacity);
        
        if(itemStackToRender.getItem() instanceof ItemBlock){
    		mc.renderEngine.func_98187_b("/terrain.png");
        }else{
        	mc.renderEngine.func_98187_b("/gui/items.png");
        }
        	
        drawTextureModelFromIcon(textureLocation, centerOfDisplay);
        
		if(displayAnalogBar){
			renderAnalogBar(mc, centerOfDisplay, analogAmount, 16);
		}
		
		if(displayNumericCounter){
			renderCounterBar(mc, centerOfDisplay, counterAmount);
		}
		
        GL11.glPopMatrix();
	}

	/**
	 * Used to Draw Analog Bar.
	 * @param mc The Minecraft Instance
	 * @param centerOfDisplay The Center Position where the bar needs to be offset From.
	 * @param analogValue The value representing how full the Bar is
	 * @param analogMax The value that represents the width of the full bar. 
	 */
	protected void renderAnalogBar(Minecraft mc, Point centerOfDisplay, int analogValue, int analogMax){
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture("/armorbarresources/ArmorBarMod_Countdown.png"));
		drawTexturedModalRect(centerOfDisplay.getX(), centerOfDisplay.getY()+16, 0, 0, analogMax, 3);
		if(analogValue > 9){
			drawTexturedModalRect(centerOfDisplay.getX()+displayAnalogOffset.getX(), centerOfDisplay.getY()+displayAnalogOffset.getY(), 0, 3, analogValue, 3);
		}else if(analogValue > 4){
			drawTexturedModalRect(centerOfDisplay.getX()+displayAnalogOffset.getX(), centerOfDisplay.getY()+displayAnalogOffset.getY(), 0, 6, analogValue, 3);
		}else{
			drawTexturedModalRect(centerOfDisplay.getX()+displayAnalogOffset.getX(), centerOfDisplay.getY()+displayAnalogOffset.getY(), 0, 9, analogValue, 3);
		}
	}
	
	/**
	 * Used to Draw Analog Bar.
	 * @param mc The Minecraft Instance
	 * @param fontRenderer The fontRenderer
	 * @param centerOfDisplay The Center Position where the bar is offset From.
	 * @param analogValue The value representing how full the Bar is
	 * @param analogMax The value that represents the width of the full bar. 
	 */
	protected void renderCounterBar(Minecraft mc, Point centerOfDisplay, int counterAmount){
		
		String displayAmount = Integer.toString(counterAmount);
		mc.fontRenderer.drawString(displayAmount, 
				centerOfDisplay.getX() + 8 - mc.fontRenderer.getStringWidth(displayAmount)/2 + displayCounterOffset.getX(),
				centerOfDisplay.getY() - displayCounterOffset.getY(),
				displayColor);
	}
	
	/**
	 * Helper Method to Convert from the Sprite Coordinate Index to the Pixel(X,Y) on the Spritesheet where the desired Sprite is.
	 * Resolution is assumed 16x16 squares.
	 * @param iconIndex
	 * @return Resolution Coordinates
	 */
	protected Point getIconCoordFromIndex(int iconIndex){
		Point point = new Point();
		int tempIndex = iconIndex;
		while(tempIndex > (32-1)){
			tempIndex-=(32-1);
			point.setY(point.getY() + 1);
		}
		
		/* Note that it is scaled by 16 to represent resolution distance that it should point to (16 pixels being the width of each icon) */
		point.setX(tempIndex*16);
		point.setY(point.getY()*16);
		return point;
	}
	
}
