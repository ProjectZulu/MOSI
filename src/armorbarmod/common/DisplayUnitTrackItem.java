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

public class DisplayUnitTrackItem extends DisplayUnitItem{
	
	int itemIDToTrack;
	int itemDamageToTrack;
	int analogUpperLimit = 64;
	int updateFrequency = 10;
	boolean displayWhenEmpty = true;
	boolean trackingTypeDurability = false;
	boolean shouldItemsHaveSameMeta = false;
	
	int trackedValue = 0;

	public DisplayUnitTrackItem(String name, int itemIDToTrack, boolean shouldDisplay, int displayColor, Point displayOffset) {
		this(name, itemIDToTrack, 0, shouldDisplay, displayColor, displayOffset);
	}
	
	public DisplayUnitTrackItem(String name, int itemIDToTrack, int itemDamageToTrack, boolean shouldDisplay,	int displayColor, Point displayOffset) {
		super(name, shouldDisplay, displayColor, displayOffset);
		this.itemIDToTrack = itemIDToTrack;
		this.itemDamageToTrack = itemDamageToTrack;
	}
	
	@Override
	public boolean shouldRender(Minecraft mc){
		//TODO add itemID is valid Check
		return super.shouldRender(mc) && (displayWhenEmpty || trackedValue > 0);
	}
	
	@Override
	public void onUpdate(Minecraft mc, int ticks) {
		super.onUpdate(mc, ticks);
		if(ticks % updateFrequency == 0){
			if(trackingTypeDurability){
				/* Variable That will hold the Item we want to render */
				ItemStack itemStackToRender = findTrackableItem(mc);

				/* Get Damage of Itemstack */
				int currentDamage = itemStackToRender.getItemDamage();
				int maxDamage = itemStackToRender.getItem().getMaxDamage();
				trackedValue = maxDamage - currentDamage;

			}else{
				/* Variable That will hold the Item we want to render */
				ItemStack itemStackToRender = new ItemStack(itemIDToTrack, 1, itemDamageToTrack);
				
				/* Count number of itemIDToTrack are in are in the Players Inventory */
				trackedValue = 0;
				ItemStack[] inventory = mc.thePlayer.inventory.mainInventory;
				for (int i = 0; i < inventory.length; i++) {
					if(inventory[i] != null && inventory[i].isItemEqual(itemStackToRender) 
							&& (!shouldItemsHaveSameMeta || inventory[i].getItemDamage() == itemDamageToTrack) ){
						trackedValue += inventory[i].stackSize;
						itemStackToRender = itemStackToRender == null ? inventory[i] : itemStackToRender ;
					}
				}
			}
		}
	}
	
	@Override
	protected int getTrackedValueForFade() {
		return trackedValue;
	}
	
	@Override
	public void renderDisplay(Minecraft mc) {
		/* Variable That will hold the Item we want to render */
		ItemStack itemStackToRender = findTrackableItem(mc);
		String textureLocation = itemStackToRender.getItem().getTextureFile();
		int analogMax = trackingTypeDurability ? itemStackToRender.getItem().getMaxDamage() : analogUpperLimit;
		int scaledAmount = mapValueToScale(trackedValue, analogMax, 16);
		renderSpecifics(mc, itemStackToRender,	textureLocation, scaledAmount, trackedValue);
	}
	
	private ItemStack findTrackableItem(Minecraft mc){
		ItemStack[] inventory = mc.thePlayer.inventory.mainInventory;
		for (int i = 0; i < inventory.length; i++) {
			if(inventory[i] != null && inventory[i].getItem().itemID == itemIDToTrack 
					&& (!shouldItemsHaveSameMeta || inventory[i].getItemDamage() == itemDamageToTrack)){
				return inventory[i];
			}
		}
		return trackingTypeDurability ? new ItemStack(itemIDToTrack, 1, Item.itemsList[itemIDToTrack].getMaxDamage()) : new ItemStack(itemIDToTrack, 1, itemDamageToTrack);
	}
	
	@Override
	public void loadProfile(EnumSet<Setting> defaultSettings){
		super.loadProfile(defaultSettings);

		for (Setting setting : defaultSettings) {
			switch (setting) {
			case FullStack:
				analogUpperLimit = 64;
				break;
			case DisplayWhenEmpty:
				displayWhenEmpty = true;
				break;
			case HideWhenEmpty:
				displayWhenEmpty = false;
				break;
			case equalItemMeta:	
				shouldItemsHaveSameMeta = true;
				break;
			case trackDurability:
				trackingTypeDurability = true;
				break;
			case trackAmount:
				trackingTypeDurability = false;
				break;
			default:
				break;
			}
		}
	}
	
	@Override
	public void getFromConfig(Configuration config) {
		super.getFromConfig(config);
		itemIDToTrack = config.get("Display Unit."+name, Integer.toString(itemIDToTrack), itemIDToTrack, "ItemID of desired Item to track").getInt(itemIDToTrack);
		itemDamageToTrack = config.get("Display Unit."+name, Integer.toString(itemDamageToTrack), itemDamageToTrack).getInt(itemDamageToTrack);
		analogUpperLimit = config.get("Display Unit."+name, Integer.toString(analogUpperLimit), analogUpperLimit).getInt(analogUpperLimit);
		updateFrequency = config.get("Display Unit."+name, Integer.toString(updateFrequency), updateFrequency).getInt(updateFrequency);

		displayWhenEmpty = config.get("Display Unit."+name, Boolean.toString(displayWhenEmpty), displayWhenEmpty).getBoolean(displayWhenEmpty);
		trackingTypeDurability = config.get("Display Unit."+name, Boolean.toString(trackingTypeDurability), trackingTypeDurability).getBoolean(trackingTypeDurability);	
		shouldItemsHaveSameMeta = config.get("Display Unit."+name, Boolean.toString(shouldItemsHaveSameMeta), shouldItemsHaveSameMeta).getBoolean(shouldItemsHaveSameMeta);	
	}	
}
