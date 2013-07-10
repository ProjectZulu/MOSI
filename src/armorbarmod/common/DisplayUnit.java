package armorbarmod.common;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.common.Configuration;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

public abstract class DisplayUnit {
	public final String name;
	boolean shouldDisplay;
	int displayColor;
	boolean displayAnalogBar; public void setDisplayAnalogBar(boolean displayAnalogBar){ this.displayAnalogBar = displayAnalogBar; }
	boolean displayNumericCounter; public void setDisplayNumericCounter(boolean displayNumericCounter){ this.displayNumericCounter = displayNumericCounter; }

	/*
	 * Offset For Display: 
	 * Horizontal is referenced from Middle of Screen, - is To The Left
	 * Vertical is referenced from Bottom of Screen, + is Upwards
	 */
	Point displayOffset;
	/* Offset for Analog Screen */
	Point displayAnalogOffset;
	/* Offset for Counter Screen */
	Point displayCounterOffset;
	protected float zLevel = 10.0F;

	int fadeRate = 0;
	float opacity = 1;
	int prevTrackedValue;
	
	public DisplayUnit(String name, boolean shouldDisplay, int displayColor, Point displayOffset){
		this.name = name;
		this.shouldDisplay = shouldDisplay;
		this.displayColor = displayColor;
		this.displayOffset = displayOffset;
	}
	
	protected abstract int getTrackedValueForFade();
	public abstract void renderDisplay(Minecraft mc);
	
	public boolean shouldRender(Minecraft mc){
		return shouldDisplay && opacity > 0 && mc.currentScreen == null;
	}
	
	public void onUpdate(Minecraft mc, int ticks){
		if(prevTrackedValue != getTrackedValueForFade() ){
			opacity = 1;
			prevTrackedValue = getTrackedValueForFade();
		}else{
			opacity -= fadeRate > 0 ? 1.0f/fadeRate : 0;
			opacity = Math.max(opacity, 0);
		}
	}
	
	/**
	 * Used to Set default values in accordance to a certain type
	 * @param profile
	 */	
	public void loadProfile(EnumSet<Setting> defaultSettings){
		for (Setting setting : defaultSettings) {
			switch (setting) {
			case FlowRight:
				displayAnalogOffset = new Point(0, 16);
				displayCounterOffset = new Point(14, -6);
				break;
			case FlowLeft:
				displayAnalogOffset = new Point(0, 16);
				displayCounterOffset = new Point(-14, -6);
				break;
			case AnalogBar:
				displayAnalogBar = true;
				break;
			case DigitalCounter:
				displayNumericCounter = true;
				break;
			default: 
				break;
			}
		}
	}
	
	/**
	 * Used to Load Config values To/From Config. Subclass should override to add desired specialized values. 
	 * @param config
	 */
	public void getFromConfig(Configuration config){
		shouldDisplay = config.get("ArmorBar."+name, "Should Display", shouldDisplay, "Controls if this DisplayUnit is enabled").getBoolean(shouldDisplay);
		displayColor = config.get("ArmorBar."+name, "Display Color", displayColor, "Controls the Color of the Numeric Counter Font").getInt(displayColor);
		displayAnalogBar = config.get("ArmorBar."+name, "Display Analog Bar", displayAnalogBar, "Toggles whether the analog bar is enabled").getBoolean(displayAnalogBar);
		displayNumericCounter = config.get("ArmorBar."+name, "Display Numeric Counter", displayNumericCounter, "Toggles whether the digital counter is enabled").getBoolean(displayNumericCounter);
		fadeRate = config.get("ArmorBar."+name, "Fade Rate", fadeRate, "Controls the amount of time [in ticks] that are required for the Display to Fade. 0 = Disabled").getInt(fadeRate);

		displayOffset.setX(config.get("ArmorBar."+name, "Main Offset X", displayOffset.getX(), "Offset for the Entire DisplayUnit. X is pos to the right").getInt(displayOffset.getX()));
		displayOffset.setY(config.get("ArmorBar."+name, "Main Offset Y", displayOffset.getY(), "Offset for the Entire DisplayUnit. Y is pos upwards").getInt(displayOffset.getY()));
		displayAnalogOffset.setX(config.get("ArmorBar."+name, "Analog Display Offset X", displayAnalogOffset.getX(), "Offset for the Analog Display Bar relative to Main").getInt(displayAnalogOffset.getX()));
		displayAnalogOffset.setY(config.get("ArmorBar."+name, "Analog Display Offset Y", displayAnalogOffset.getY(), "Offset for the Analog Display Bar relative to Main").getInt(displayAnalogOffset.getY()));
		displayCounterOffset.setX(config.get("ArmorBar."+name, "Counter Display Offset X", displayCounterOffset.getX(), "Offset for the Digital Display Bar relative to Main").getInt(displayCounterOffset.getX()));
		displayCounterOffset.setY(config.get("ArmorBar."+name, "Counter Display Offset Y", displayCounterOffset.getY(), "Offset for the Digital Display Bar relative to Main").getInt(displayCounterOffset.getY()));
	}
	
	/**
	 * Fundamental Minecraft Function to Draw a Texture in the World. Copied from Minecraft Code, not sure where, is everywhere.
	 */
	protected void drawTexturedModalRect(int par1, int par2, int par3, int par4, int par5, int par6){
		float var7 = 0.00390625F;
		float var8 = 0.00390625F;

		Tessellator var9 = Tessellator.instance;
		var9.startDrawingQuads();
		var9.addVertexWithUV((double)(par1 + 0), (double)(par2 + par6), (double)this.zLevel, (double)((float)(par3 + 0) * var7), (double)((float)(par4 + par6) * var8));
		var9.addVertexWithUV((double)(par1 + par5), (double)(par2 + par6), (double)this.zLevel, (double)((float)(par3 + par5) * var7), (double)((float)(par4 + par6) * var8));
		var9.addVertexWithUV((double)(par1 + par5), (double)(par2 + 0), (double)this.zLevel, (double)((float)(par3 + par5) * var7), (double)((float)(par4 + 0) * var8));
		var9.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), (double)this.zLevel, (double)((float)(par3 + 0) * var7), (double)((float)(par4 + 0) * var8));
		var9.draw();        	
	}
	
	protected void drawTextureModelFromIcon(Icon icon, Point screenPosition){
        final float var13 = icon.getMinU();
        final float var15 = icon.getMaxU();
        final float var17 = icon.getMinV();
        final float var19 = icon.getMaxV();
    	final float zLevel = 10.0F;

    	Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
        
        tessellator.addVertexWithUV(screenPosition.getX() + 00.0D, screenPosition.getY() + 16.0D, zLevel, var13, var19);
        tessellator.addVertexWithUV(screenPosition.getX() + 16.0D, screenPosition.getY() + 16.0D, zLevel, var15, var19);
        tessellator.addVertexWithUV(screenPosition.getX() + 16.0D, screenPosition.getY() + 00.0D, zLevel, var15, var17);
        tessellator.addVertexWithUV(screenPosition.getX() + 00.0D, screenPosition.getY() + 00.0D, zLevel, var13, var17);
        tessellator.draw();
	}
	
	/** Helper method that Maps the real value provided (representing damage typically) to a different scale (typically resolution, 16)
	 * @param realValue represents value in Set 1
	 * @param realMax is the max value in set 1, min value is assumed zero.
	 * @param scaleMax is the max value in set 2, min value is assumed zero.
	 * @return realValue in set 2
	 */
	protected int mapValueToScale(int realValue, int realMax, int scaleMax){
		return realValue > realMax ? scaleMax : realValue < 0 ? 0 : (int)(((float)realValue)/realMax*scaleMax);
	}
}
