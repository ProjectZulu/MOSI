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

public class MOSIDisplayTicker implements ITickHandler{
	public static int inGameTicks = 0;
	protected float zLevel = 10.0F;
	private int maxBuffLength;

	static List<DisplayUnit> displayList = new ArrayList();

	@Override
	public EnumSet<TickType> ticks() { return EnumSet.of(TickType.RENDER); }
	@Override
	public String getLabel() { return null;	}

	public void tickStart(EnumSet<TickType> type, Object... tickData){}
	
	public void tickEnd(EnumSet<TickType> type, Object... tickData){
		if(Minecraft.getMinecraft().thePlayer != null){
			Minecraft mc = Minecraft.getMinecraft();
			for (DisplayUnit displayUnit : displayList) {
				displayUnit.onUpdate(mc, inGameTicks);
				if(displayUnit.shouldRender(mc)){
					displayUnit.renderDisplay(mc);
				}
			}			
		}
		inGameTicks++;
	}
}
