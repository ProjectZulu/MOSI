package armorbarmod.common;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.src.ModLoader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ArmorBarDisplayTicker implements ITickHandler{
	public static int inGameTicks = 0;
	protected float zLevel = 10.0F;
	private int maxBuffLength;

	static List<DisplayUnit> displayList = new ArrayList();

	public boolean displayArrow = mod_ArmorBarMod.displayArrow;
	public int horizontalOffsetFromMiddleArrow = mod_ArmorBarMod.horizontalOffsetFromMiddleArrow;
	public int verticalOffsetFromBottomArrow = mod_ArmorBarMod.verticalOffsetFromBottomArrow;
	public int displayStyleArrow = mod_ArmorBarMod.displayStyleArrow;
	public int fontColorArrow = mod_ArmorBarMod.fontColorArrow;
	public int horizontalStringOffsetArrow = mod_ArmorBarMod.horizontalStringOffsetArrow;
	public int verticalStringOffsetArrow = mod_ArmorBarMod.verticalStringOffsetArrow;
	public int numberOfArrows;
	public int maxOfArrows = mod_ArmorBarMod.maxOfArrows;

	public boolean displayGenericItem1 = mod_ArmorBarMod.displayGenericItem1;
	public int genericItem1ShiftedID = mod_ArmorBarMod.genericItem1ShiftedID;
	public int trackingTypeGenericItem1 = mod_ArmorBarMod.trackingTypeGenericItem1;
	public int horizontalOffsetFromMiddleGenericItem1 = mod_ArmorBarMod.horizontalOffsetFromMiddleGenericItem1;
	public int verticalOffsetFromBottomGenericItem1 = mod_ArmorBarMod.verticalOffsetFromBottomGenericItem1;
	public int displayStyleGenericItem1 = mod_ArmorBarMod.displayStyleGenericItem1;
	public int fontColorGenericItem1 = mod_ArmorBarMod.fontColorGenericItem1;
	public int horizontalStringOffsetGenericItem1 = mod_ArmorBarMod.horizontalStringOffsetGenericItem1;
	public int verticalStringOffsetGenericItem1 = mod_ArmorBarMod.verticalStringOffsetGenericItem1;
	public int numberOfGenericItem1;
	public int maxOfGenericItem1 = mod_ArmorBarMod.maxOfGenericItem1;
	
	public boolean displayGenericItem2 = mod_ArmorBarMod.displayGenericItem2;
	public int genericItem2ShiftedID = mod_ArmorBarMod.genericItem2ShiftedID;
	public int trackingTypeGenericItem2 = mod_ArmorBarMod.trackingTypeGenericItem2;
	public int horizontalOffsetFromMiddleGenericItem2 = mod_ArmorBarMod.horizontalOffsetFromMiddleGenericItem2;
	public int verticalOffsetFromBottomGenericItem2 = mod_ArmorBarMod.verticalOffsetFromBottomGenericItem2;
	public int displayStyleGenericItem2 = mod_ArmorBarMod.displayStyleGenericItem2;
	public int fontColorGenericItem2 = mod_ArmorBarMod.fontColorGenericItem2;
	public int horizontalStringOffsetGenericItem2 = mod_ArmorBarMod.horizontalStringOffsetGenericItem2;
	public int verticalStringOffsetGenericItem2 = mod_ArmorBarMod.verticalStringOffsetGenericItem2;
	public int numberOfGenericItem2;
	public int maxOfGenericItem2 = mod_ArmorBarMod.maxOfGenericItem2;

	public boolean displayGenericItem3 = mod_ArmorBarMod.displayGenericItem3;
	public int genericItem3ShiftedID = mod_ArmorBarMod.genericItem3ShiftedID;
	public int trackingTypeGenericItem3 = mod_ArmorBarMod.trackingTypeGenericItem3;
	public int horizontalOffsetFromMiddleGenericItem3 = mod_ArmorBarMod.horizontalOffsetFromMiddleGenericItem3;
	public int verticalOffsetFromBottomGenericItem3 = mod_ArmorBarMod.verticalOffsetFromBottomGenericItem3;
	public int displayStyleGenericItem3 = mod_ArmorBarMod.displayStyleGenericItem3;
	public int fontColorGenericItem3 = mod_ArmorBarMod.fontColorGenericItem3;
	public int horizontalStringOffsetGenericItem3 = mod_ArmorBarMod.horizontalStringOffsetGenericItem3;
	public int verticalStringOffsetGenericItem3 = mod_ArmorBarMod.verticalStringOffsetGenericItem3;
	public int numberOfGenericItem3;
	public int maxOfGenericItem3 = mod_ArmorBarMod.maxOfGenericItem3;

	@Override
	public EnumSet<TickType> ticks() { return EnumSet.of(TickType.RENDER); }
	@Override
	public String getLabel() { return null;	}

	public void tickStart(EnumSet<TickType> type, Object... tickData){}
	
	public void tickEnd(EnumSet<TickType> type, Object... tickData){
		if(ModLoader.getMinecraftInstance().thePlayer != null){
			
			EntityPlayer player = ModLoader.getMinecraftInstance().thePlayer;
			Minecraft mc = ModLoader.getMinecraftInstance();
			FontRenderer var2 = mc.fontRenderer;
			ScaledResolution var3 = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
			int scalewidth = var3.getScaledWidth();
			int scaleHeight = var3.getScaledHeight();
			
			for (DisplayUnit displayUnit : displayList) {
				displayUnit.onUpdate(mc, inGameTicks);
				if(displayUnit.shouldRender(mc)){
					displayUnit.renderDisplay(mc);
				}
			}
			
			//Render GenericItem1 Counter
			if(displayGenericItem1){
				this.numberOfGenericItem1 = 0;
				/* Variable That will hold the Item we want to render, which We find based on ItemID.*/
				ItemStack itemStackToRender = null;

				//Count Number of Item in Inventory
				ItemStack[] inventory = player.inventory.mainInventory;
				for (int i = 0; i < inventory.length; i++) {
					if(inventory[i] != null && inventory[i].getItem().itemID == genericItem1ShiftedID){
						numberOfGenericItem1 += inventory[i].stackSize;
						itemStackToRender = itemStackToRender == null ? inventory[i] : itemStackToRender ;
					}
				}
				if(itemStackToRender != null){
					Item itemToRender = itemStackToRender.getItem();
					int iconIndex = itemStackToRender.getIconIndex();
					String textureLocation = itemToRender.getTextureFile();
					int currentDamage = itemStackToRender.getItemDamage();
					int maxDamage = itemToRender.getMaxDamage();

					int health;
					if(trackingTypeGenericItem1 == 0){
						health = mapAmounttoMaxto16(numberOfGenericItem1, maxOfGenericItem1);
					}else{
						health = mapDamagetoHealthto16(currentDamage, maxDamage);
					}

					int iconXCoord = 0;
					int iconYCoord = 0;
					int tempIndex = iconIndex;
					while(tempIndex > 15){
						tempIndex-=16;
						iconYCoord += 1;
					}
					iconXCoord = tempIndex;

					iconXCoord*=16;
					iconYCoord*=16;


					GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture( textureLocation ));
					this.drawTexturedModalRect(scalewidth/2+horizontalOffsetFromMiddleGenericItem1, scaleHeight-(16+verticalOffsetFromBottomGenericItem1), iconXCoord, iconYCoord, 16, 16);

					if(displayStyleGenericItem1 == 0 || displayStyleGenericItem1 == 2){
						GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture("/mods/ArmorBarMod_Countdown.png"));
						this.drawTexturedModalRect(scalewidth/2+horizontalOffsetFromMiddleGenericItem1, scaleHeight-verticalOffsetFromBottomGenericItem1, 0, 0, 16, 3);
						
						if(health > 9){
							this.drawTexturedModalRect(scalewidth/2+horizontalOffsetFromMiddleGenericItem1, scaleHeight-verticalOffsetFromBottomGenericItem1, 0, 3, health, 3);
						}else if(health > 4){
							this.drawTexturedModalRect(scalewidth/2+horizontalOffsetFromMiddleGenericItem1, scaleHeight-verticalOffsetFromBottomGenericItem1, 0, 6, health, 3);
						}else{
							this.drawTexturedModalRect(scalewidth/2+horizontalOffsetFromMiddleGenericItem1, scaleHeight-verticalOffsetFromBottomGenericItem1, 0, 9, health, 3);
						}
					}

					if(displayStyleGenericItem1 == 1 || displayStyleGenericItem1 == 2){
						String var9;
						if(this.trackingTypeGenericItem1 == 0){
							var9 = Integer.toString(numberOfGenericItem1);
						}else{
							var9 = Integer.toString(maxDamage - currentDamage);
						}
						var2.drawString(var9, 
								scalewidth/2+horizontalOffsetFromMiddleGenericItem1 + var2.getStringWidth(var9) + horizontalStringOffsetGenericItem1 / 2,
								scaleHeight-(8+verticalOffsetFromBottomGenericItem1) - verticalStringOffsetGenericItem1,
								fontColorGenericItem1);
					}
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				}
			}
			
			//Render GenericItem2 Counter
			if(displayGenericItem2){
				
				this.numberOfGenericItem2 = 0;
				/* Variable That will hold the Item we want to render, which We find based on ItemID.*/
				ItemStack itemStackToRender = null;

				//Count Number of Item in Inventory
				ItemStack[] inventory = player.inventory.mainInventory;
				for (int i = 0; i < inventory.length; i++) {
					if(inventory[i] != null && inventory[i].getItem().itemID == genericItem2ShiftedID){
						numberOfGenericItem2 += inventory[i].stackSize;
						itemStackToRender = itemStackToRender == null ? inventory[i] : itemStackToRender ;
					}
				}
				if(itemStackToRender != null){
					Item itemToRender = itemStackToRender.getItem();
					int iconIndex = itemStackToRender.getIconIndex();
					String textureLocation = itemToRender.getTextureFile();
					int currentDamage = itemStackToRender.getItemDamage();
					int maxDamage = itemToRender.getMaxDamage();

					int health;
					if(trackingTypeGenericItem2 == 0){
						health = mapAmounttoMaxto16(numberOfGenericItem2, maxOfGenericItem2);
					}else{
						health = mapDamagetoHealthto16(currentDamage, maxDamage);
					}

					int iconXCoord = 0;
					int iconYCoord = 0;
					int tempIndex = iconIndex;
					while(tempIndex > 15){
						tempIndex-=16;
						iconYCoord += 1;
					}
					iconXCoord = tempIndex;

					iconXCoord*=16;
					iconYCoord*=16;


					GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture( textureLocation ));
					this.drawTexturedModalRect(scalewidth/2+horizontalOffsetFromMiddleGenericItem2, scaleHeight-(16+verticalOffsetFromBottomGenericItem2), iconXCoord, iconYCoord, 16, 16);

					if(displayStyleGenericItem2 == 0 || displayStyleGenericItem2 == 2){
						GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture("/mods/ArmorBarMod_Countdown.png"));
						this.drawTexturedModalRect(scalewidth/2+horizontalOffsetFromMiddleGenericItem2, scaleHeight-verticalOffsetFromBottomGenericItem2, 0, 0, 16, 3);
						
						if(health > 9){
							this.drawTexturedModalRect(scalewidth/2+horizontalOffsetFromMiddleGenericItem2, scaleHeight-verticalOffsetFromBottomGenericItem2, 0, 3, health, 3);
						}else if(health > 4){
							this.drawTexturedModalRect(scalewidth/2+horizontalOffsetFromMiddleGenericItem2, scaleHeight-verticalOffsetFromBottomGenericItem2, 0, 6, health, 3);
						}else{
							this.drawTexturedModalRect(scalewidth/2+horizontalOffsetFromMiddleGenericItem2, scaleHeight-verticalOffsetFromBottomGenericItem2, 0, 9, health, 3);
						}
					}

					if(displayStyleGenericItem2 == 1 || displayStyleGenericItem2 == 2){
						String var9;
						if(this.trackingTypeGenericItem2 == 0){
							var9 = Integer.toString(numberOfGenericItem2);
						}else{
							var9 = Integer.toString(maxDamage - currentDamage);
						}
						var2.drawString(var9, 
								scalewidth/2+horizontalOffsetFromMiddleGenericItem2 + var2.getStringWidth(var9) + horizontalStringOffsetGenericItem2 / 2,
								scaleHeight-(8+verticalOffsetFromBottomGenericItem2) - verticalStringOffsetGenericItem2,
								fontColorGenericItem2);
					}
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				}
			}


			//Render GenericItem3 Counter
			if(displayGenericItem3){
				
				this.numberOfGenericItem3 = 0;
				/* Variable That will hold the Item we want to render, which We find based on ItemID.*/
				ItemStack itemStackToRender = null;

				//Count Number of Item in Inventory
				ItemStack[] inventory = player.inventory.mainInventory;
				for (int i = 0; i < inventory.length; i++) {
					if(inventory[i] != null && inventory[i].getItem().itemID == genericItem3ShiftedID){
						numberOfGenericItem3 += inventory[i].stackSize;
						itemStackToRender = itemStackToRender == null ? inventory[i] : itemStackToRender ;
					}
				}
				if(itemStackToRender != null){
					Item itemToRender = itemStackToRender.getItem();
					int iconIndex = itemStackToRender.getIconIndex();
					String textureLocation = itemToRender.getTextureFile();
					int currentDamage = itemStackToRender.getItemDamage();
					int maxDamage = itemToRender.getMaxDamage();

					int health;
					if(trackingTypeGenericItem3 == 0){
						health = mapAmounttoMaxto16(numberOfGenericItem3, maxOfGenericItem3);
					}else{
						health = mapDamagetoHealthto16(currentDamage, maxDamage);
					}

					int iconXCoord = 0;
					int iconYCoord = 0;
					int tempIndex = iconIndex;
					while(tempIndex > 15){
						tempIndex-=16;
						iconYCoord += 1;
					}
					iconXCoord = tempIndex;

					iconXCoord*=16;
					iconYCoord*=16;


					GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture( textureLocation ));
					this.drawTexturedModalRect(scalewidth/2+horizontalOffsetFromMiddleGenericItem3, scaleHeight-(16+verticalOffsetFromBottomGenericItem3), iconXCoord, iconYCoord, 16, 16);

					if(displayStyleGenericItem3 == 0 || displayStyleGenericItem3 == 2){
						GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture("/mods/ArmorBarMod_Countdown.png"));
						this.drawTexturedModalRect(scalewidth/2+horizontalOffsetFromMiddleGenericItem3, scaleHeight-verticalOffsetFromBottomGenericItem3, 0, 0, 16, 3);
						
						if(health > 9){
							this.drawTexturedModalRect(scalewidth/2+horizontalOffsetFromMiddleGenericItem3, scaleHeight-verticalOffsetFromBottomGenericItem3, 0, 3, health, 3);
						}else if(health > 4){
							this.drawTexturedModalRect(scalewidth/2+horizontalOffsetFromMiddleGenericItem3, scaleHeight-verticalOffsetFromBottomGenericItem3, 0, 6, health, 3);
						}else{
							this.drawTexturedModalRect(scalewidth/2+horizontalOffsetFromMiddleGenericItem3, scaleHeight-verticalOffsetFromBottomGenericItem3, 0, 9, health, 3);
						}
					}

					if(displayStyleGenericItem3 == 1 || displayStyleGenericItem3 == 2){
						String var9;
						if(this.trackingTypeGenericItem3 == 0){
							var9 = Integer.toString(numberOfGenericItem3);
						}else{
							var9 = Integer.toString(maxDamage - currentDamage);
						}
						var2.drawString(var9, 
								scalewidth/2+horizontalOffsetFromMiddleGenericItem3 + var2.getStringWidth(var9) + horizontalStringOffsetGenericItem3 / 2,
								scaleHeight-(8+verticalOffsetFromBottomGenericItem3) - verticalStringOffsetGenericItem3,
								fontColorGenericItem3);
					}
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				}
			}

			
		}

		inGameTicks++;
	}
	public void drawTexturedModalRect(int par1, int par2, int par3, int par4, int par5, int par6)
	{
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

	public int mapDurationTo18(int duration, int maxDuration){

		float scaledDuration = duration;
		//		float maxDuration

		if(duration > maxDuration){
			return 18;
		}
		if(duration < 0){
			return 0;
		}

		return (int)( (float)(duration)/(float)(maxDuration)*18 ); 
	}

	public int mapDamagetoHealthto16(int damage, int maxDamage){

		int health = maxDamage - damage;

		if(health > maxDamage){
			return 16;
		}
		if(health <= 0){
			return 0;
		}
		return (int)( (float)health/(float)maxDamage*16 );
	}
	
	public int mapAmounttoMaxto16(int amount, int maxAmount){

//		int health = maxDamage - damage;

		if(amount > maxAmount){
			return 16;
		}
		if(amount <= 0){
			return 0;
		}
		return (int)( (float)amount/(float)maxAmount*16 );
	}



	public void drawPotionBuffAndDuration(Minecraft mc, EntityPlayer player, int scalewidth, int scaleHeight, int lengthOfBuffBar, int buffNumber,
			int scaledDuration, int iconXCoord, int iconYCoord){
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture("/gui/inventory.png"));
		this.drawTexturedModalRect(scalewidth/2-lengthOfBuffBar/2+18*buffNumber, scaleHeight-18*3-7, iconXCoord, iconYCoord, 18, 18);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture("/mods/BuffBar_Countdown.png"));
		//Draw BackGround of CountDown Bar
		this.drawTexturedModalRect(scalewidth/2-lengthOfBuffBar/2+18*buffNumber, scaleHeight-18*3-7+18, 0, 0, 18, 3);
		if(scaledDuration > 9){
			this.drawTexturedModalRect(scalewidth/2-lengthOfBuffBar/2+18*buffNumber, scaleHeight-18*3-7+18, 0, 3, scaledDuration, 3);
		}else if(scaledDuration > 4){
			this.drawTexturedModalRect(scalewidth/2-lengthOfBuffBar/2+18*buffNumber, scaleHeight-18*3-7+18, 0, 3+3, scaledDuration, 3);
		}else{
			this.drawTexturedModalRect(scalewidth/2-lengthOfBuffBar/2+18*buffNumber, scaleHeight-18*3-7+18, 0, 3+6, scaledDuration, 3);
		}
		//Foreground of CountdownBar
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

}
