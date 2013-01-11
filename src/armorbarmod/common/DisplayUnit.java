package armorbarmod.common;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

public abstract class DisplayUnit {
	String name;
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
		super();
		this.name = name;
		this.shouldDisplay = shouldDisplay;
		this.displayColor = displayColor;
		this.displayOffset = displayOffset;
	}
	
	protected abstract int getTrackedValueForFade();
	public abstract void renderDisplay(Minecraft mc);
	
	public boolean shouldRender(Minecraft mc){
		return shouldDisplay && opacity > 0;
	}
	
	public void onUpdate(Minecraft mc, int ticks) {
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
				displayCounterOffset = new Point(16, 4-8);
				break;
			case FlowLeft:
				displayAnalogOffset = new Point(0, 16);
				displayCounterOffset = new Point(-16, 4-8);
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
		shouldDisplay = config.get("Display Unit."+name, Boolean.toString(shouldDisplay), shouldDisplay).getBoolean(shouldDisplay);
		displayColor = config.get("Display Unit."+name, Integer.toString(displayColor), displayColor).getInt(displayColor);
		displayAnalogBar = config.get("Display Unit."+name, Boolean.toString(displayAnalogBar), displayAnalogBar).getBoolean(displayAnalogBar);
		displayNumericCounter = config.get("Display Unit."+name, Boolean.toString(displayNumericCounter), displayNumericCounter).getBoolean(displayNumericCounter);
		
		displayOffset.setX(config.get("Display Unit."+name, displayOffset.toString()+".X", displayOffset.getX()).getInt(displayOffset.getX()));
		displayOffset.setY(config.get("Display Unit."+name, displayOffset.toString()+".Y", displayOffset.getY()).getInt(displayOffset.getY()));
		displayAnalogOffset.setX(config.get("Display Unit."+name, displayAnalogOffset.toString()+".X", displayAnalogOffset.getX()).getInt(displayAnalogOffset.getX()));
		displayAnalogOffset.setY(config.get("Display Unit."+name, displayAnalogOffset.toString()+".Y", displayAnalogOffset.getY()).getInt(displayAnalogOffset.getY()));
		displayCounterOffset.setX(config.get("Display Unit."+name, displayCounterOffset.toString()+".X", displayCounterOffset.getX()).getInt(displayCounterOffset.getX()));
		displayCounterOffset.setY(config.get("Display Unit."+name, displayCounterOffset.toString()+".Y", displayCounterOffset.getY()).getInt(displayCounterOffset.getY()));
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
