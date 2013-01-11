package armorbarmod.common;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

public class DisplayUnitTrackItem extends DisplayUnitItem{
	
	int itemIDToTrack;
	int itemMetaToTrack;
	int analogUpperLimit = 64;
	int updateFrequency = 10;
	boolean displayWhenEmpty = true;
	boolean trackDurability = false;
	
	int numberOfTracked = 0;

	public DisplayUnitTrackItem(String name, int itemIDToTrack, boolean shouldDisplay, int displayColor, Point displayOffset, Point displayAnalogOffset, Point displayCounterOffset) {
		this(name, itemIDToTrack, 0, shouldDisplay, displayColor, displayOffset, displayAnalogOffset, displayCounterOffset);
	}
	
	public DisplayUnitTrackItem(String name, int itemIDToTrack, int itemMetaToTrack, boolean shouldDisplay,	int displayColor, Point displayOffset, Point displayAnalogOffset, Point displayCounterOffset) {
		super(name, shouldDisplay, displayColor, displayOffset, displayAnalogOffset, displayCounterOffset);
		this.itemIDToTrack = itemIDToTrack;
		this.itemMetaToTrack = itemMetaToTrack;
	}
	//TODO Delete this methods
	public DisplayUnitTrackItem devTestingMethod(){
		trackDurability = true;
		return this;
	}
	
	@Override
	public boolean shouldRender(Minecraft mc){
		//TODO add itemID is valid Check
		return super.shouldRender(mc) && (displayWhenEmpty || numberOfTracked > 0);
	}
	
	@Override
	public void onUpdate(Minecraft mc, int ticks) {
		if(!trackDurability && ticks % updateFrequency == 0){
			/* Variable That will hold the Item we want to render */
			ItemStack itemStackToRender = new ItemStack(itemIDToTrack, 1, itemMetaToTrack);
			
			/* Count number of itemIDToTrackk are in are in the Players Inventory */
			numberOfTracked = 0;
			ItemStack[] inventory = mc.thePlayer.inventory.mainInventory;
			for (int i = 0; i < inventory.length; i++) {
				if(inventory[i] != null && inventory[i].isItemEqual(itemStackToRender) && inventory[i].getItemDamage() == itemMetaToTrack ){
					numberOfTracked += inventory[i].stackSize;
					itemStackToRender = itemStackToRender == null ? inventory[i] : itemStackToRender ;
				}
			}
		}
	}
	
	@Override
	public void renderDisplay(Minecraft mc) {
		if(trackDurability){
			/* Variable That will hold the Item we want to render */
			ItemStack itemStackToRender = findTrackableItem(mc);
			String textureLocation = itemStackToRender.getItem().getTextureFile();

			/* Get Damage of Itemstack */
			int currentDamage = itemStackToRender.getItemDamage();
			int maxDamage = itemStackToRender.getItem().getMaxDamage();
			int scaledAmount = mapValueToScale(currentDamage-maxDamage, maxDamage, 16);
			renderSpecifics(mc, itemStackToRender,	textureLocation, scaledAmount, numberOfTracked);
		}else{
			/* Variable That will hold the Item we want to render */
			ItemStack itemStackToRender = new ItemStack(itemIDToTrack, 1, itemMetaToTrack);
			String textureLocation = itemStackToRender.getItem().getTextureFile();

			int scaledAmount = mapValueToScale(numberOfTracked, analogUpperLimit, 16);
			renderSpecifics(mc, itemStackToRender,	textureLocation, scaledAmount, numberOfTracked);
		}
		
	}
	
	private ItemStack findTrackableItem(Minecraft mc){
		ItemStack[] inventory = mc.thePlayer.inventory.mainInventory;
		for (int i = 0; i < inventory.length; i++) {
			if(inventory[i] != null && inventory[i].getItem().itemID == itemIDToTrack){
				return inventory[i];
			}
		}
		return new ItemStack(itemIDToTrack, 1, itemMetaToTrack);
	}
	
	@Override
	public void getFromConfig(Configuration config) {
		super.getFromConfig(config);
		itemIDToTrack = config.get("Display Unit."+name, Integer.toString(itemIDToTrack), itemIDToTrack).getInt(itemIDToTrack);
		itemMetaToTrack = config.get("Display Unit."+name, Integer.toString(itemMetaToTrack), itemMetaToTrack).getInt(itemMetaToTrack);
		analogUpperLimit = config.get("Display Unit."+name, Integer.toString(analogUpperLimit), analogUpperLimit).getInt(analogUpperLimit);
		displayWhenEmpty = config.get("Display Unit."+name, Boolean.toString(displayWhenEmpty), displayWhenEmpty).getBoolean(displayWhenEmpty);
		trackDurability = config.get("Display Unit."+name, Boolean.toString(trackDurability), trackDurability).getBoolean(trackDurability);	
		updateFrequency = config.get("Display Unit."+name, Integer.toString(updateFrequency), updateFrequency).getInt(updateFrequency);
	}
	
}
