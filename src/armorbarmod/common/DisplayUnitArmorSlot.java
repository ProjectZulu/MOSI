package armorbarmod.common;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

public class DisplayUnitArmorSlot extends DisplayUnitItem{
	
	int armorSlot;
	int itemHealth;
	int updateFrequency = 10;

	public DisplayUnitArmorSlot(String name, int armorSlot, boolean shouldDisplay, int displayColor, Point displayOffset){
		super(name, shouldDisplay, displayColor, displayOffset);
		this.armorSlot = armorSlot;
	}
	
	@Override
	public boolean shouldRender(Minecraft mc){
		return super.shouldRender(mc) && mc.thePlayer.inventory.armorInventory[armorSlot] != null;
	}
	
	@Override
	public void onUpdate(Minecraft mc, int ticks) { 
		super.onUpdate(mc, ticks); 
		if(ticks % updateFrequency == 0){
			/* Variable That will hold the Item we want to render */
			ItemStack itemStackToRender = mc.thePlayer.inventory.armorInventory[armorSlot];
			if(itemStackToRender != null){
				/* Get Damage of Itemstack */
				int currentDamage = itemStackToRender.getItemDamage();
				int maxDamage = itemStackToRender.getItem().getMaxDamage();
				
				/* Store health in Tracked value to Control fade effect */
				itemHealth = maxDamage - currentDamage;
			}
		}
	}
	
	@Override
	protected int getTrackedValueForFade() {
		return itemHealth;
	}
	
	@Override
	public void renderDisplay(Minecraft mc) {		
		/* Get Itemstack to Render */
		ItemStack itemStackToRender = mc.thePlayer.inventory.armorInventory[armorSlot];
		String textureLocation = itemStackToRender.getItem().getTextureFile();
		
		/* Get Damage of Itemstack */
		int maxDamage = itemStackToRender.getItem().getMaxDamage();
		int scaledHealth = mapValueToScale(itemHealth, maxDamage, 16);
		
		/* Render bar representing health of the Item */
		renderSpecifics(mc, itemStackToRender, textureLocation, scaledHealth, itemHealth);
	}
	
	@Override
	public void getFromConfig(Configuration config) {
		super.getFromConfig(config);
		updateFrequency = config.get("ArmorBar."+name, "Update Frequency", updateFrequency, "Controls how often this DisplayUnit will Update").getInt(updateFrequency);
	}
	
}
