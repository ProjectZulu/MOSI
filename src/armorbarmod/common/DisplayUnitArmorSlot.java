package armorbarmod.common;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

public class DisplayUnitArmorSlot extends DisplayUnitItem{
	
	int armorSlot;
	
	public DisplayUnitArmorSlot(String name, int armorSlot, boolean shouldDisplay, int displayColor, Point displayOffset, Point displayAnalogOffset, Point displayCounterOffset){
		super(name, shouldDisplay, displayColor, displayOffset, displayAnalogOffset, displayCounterOffset);
		this.armorSlot = armorSlot;
	}
	
	@Override
	public boolean shouldRender(Minecraft mc){
		return super.shouldRender(mc) && mc.thePlayer.inventory.armorInventory[armorSlot] != null;
	}
	
	@Override
	public void onUpdate(Minecraft mc, int ticks) {}
	
	@Override
	public void renderDisplay(Minecraft mc) {		
		/* Get Itemstack to Render */
		ItemStack itemStackToRender = mc.thePlayer.inventory.armorInventory[armorSlot];
		String textureLocation = itemStackToRender.getItem().getTextureFile();

		/* Get Damage of Itemstack */
		int currentDamage = itemStackToRender.getItemDamage();
		int maxDamage = itemStackToRender.getItem().getMaxDamage();
		int health = mapValueToScale(currentDamage-maxDamage, maxDamage, 16);

		renderSpecifics(mc, itemStackToRender, textureLocation, health, maxDamage - currentDamage);
	}
	
	@Override
	public void getFromConfig(Configuration config) {
		super.getFromConfig(config);
		armorSlot = config.get("Display Unit."+name, Integer.toString(armorSlot), armorSlot).getInt(armorSlot);
	}
	
}
