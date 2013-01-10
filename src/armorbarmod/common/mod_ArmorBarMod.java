package armorbarmod.common;

import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "mod_ArmorBarMod", name = "Armor Bar Mod", version = "0.5.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)

public class mod_ArmorBarMod {
	
	public static boolean displayFeet = true;
	public static int horizontalOffsetFromMiddleFeet = 95;
	public static int verticalOffsetFromBottomFeet = 16*0+4;
	public static int displayStyleFeet = 0;
	public static int fontColorFeet = 1030655;
	public static int horizontalStringOffsetFeet = 8;
	public static int verticalStringOffsetFeet = 4;

	public static boolean displayLegs = true;
	public static int horizontalOffsetFromMiddleLegs = 95;
	public static int verticalOffsetFromBottomLegs = 16*1+4+2;
	public static int displayStyleLegs = 0;
	public static int fontColorLegs = 1030655;
	public static int horizontalStringOffsetLegs = 8;
	public static int verticalStringOffsetLegs = 4;

	public static boolean displayChest = true;
	public static int horizontalOffsetFromMiddleChest = -111;
	public static int verticalOffsetFromBottomChest = 16*0+4;
	public static int displayStyleChest = 0;
	public static int fontColorChest = 1030655;
	public static int horizontalStringOffsetChest = -48;
	public static int verticalStringOffsetChest = 4;

	public static boolean displayHead = true;
	public static int horizontalOffsetFromMiddleHead = -111;
	public static int verticalOffsetFromBottomHead = 16*1+4+2;
	public static int displayStyleHead = 0;
	public static int fontColorHead = 1030655;
	public static int horizontalStringOffsetHead = -48;
	public static int verticalStringOffsetHead = 4;

	public static boolean displayArrow = true;
	public static int horizontalOffsetFromMiddleArrow = -111-32;
	public static int verticalOffsetFromBottomArrow = 16*0+4;
	public static int displayStyleArrow = 1;
	public static int fontColorArrow = 1030655;
	public static int horizontalStringOffsetArrow = -48-16-4;
	public static int verticalStringOffsetArrow = 4;
	public static int numberOfArrows;
	public static int maxOfArrows = 64;

	public static boolean displayGenericItem1 = false;
	public static int genericItem1ShiftedID = Item.coal.shiftedIndex;
	public static int trackingTypeGenericItem1 = 0;
	public static int horizontalOffsetFromMiddleGenericItem1 = -111-32;
	public static int verticalOffsetFromBottomGenericItem1 = 16*1+4+2;
	public static int displayStyleGenericItem1 = 1;
	public static int fontColorGenericItem1 = 1030655;
	public static int horizontalStringOffsetGenericItem1 = -48-16-4;
	public static int verticalStringOffsetGenericItem1 = 4;
	public static int numberOfGenericItem1;
	public static int maxOfGenericItem1 = 64;
	
	public static boolean displayGenericItem2 = false;
	public static int genericItem2ShiftedID = Item.diamond.shiftedIndex;
	public static int trackingTypeGenericItem2 = 0;
	public static int horizontalOffsetFromMiddleGenericItem2 = -111-32;
	public static int verticalOffsetFromBottomGenericItem2 = 16*2+4*2+2;
	public static int displayStyleGenericItem2 = 1;
	public static int fontColorGenericItem2 = 1030655;
	public static int horizontalStringOffsetGenericItem2 = -48-16-4;
	public static int verticalStringOffsetGenericItem2 = 4;
	public static int numberOfGenericItem2;
	public static int maxOfGenericItem2 = 64;

	public static boolean displayGenericItem3 = false;
	public static int genericItem3ShiftedID = Item.pickaxeDiamond.shiftedIndex;
	public static int trackingTypeGenericItem3 = 0;
	public static int horizontalOffsetFromMiddleGenericItem3 = -111-32;
	public static int verticalOffsetFromBottomGenericItem3 = 16*3+4*3+2;
	public static int displayStyleGenericItem3 = 1;
	public static int fontColorGenericItem3 = 1030655;
	public static int horizontalStringOffsetGenericItem3 = -48-16-4;
	public static int verticalStringOffsetGenericItem3 = 4;
	public static int numberOfGenericItem3;
	public static int maxOfGenericItem3 = 64;
	
	@Instance("mod_ArmorBarMod")
	public static mod_ArmorBarMod modInstance;
	
	@SidedProxy(clientSide = "armorbarmod.client.ClientProxyArmorBarMod", serverSide = "armorbarmod.common.CommonProxyArmorBarMod")
	public static CommonProxyArmorBarMod proxy;

	
	@PreInit
	public void preInit(FMLPreInitializationEvent event){
        Configuration armorConfig = new Configuration(event.getSuggestedConfigurationFile());
        armorConfig.load();

        /* Render Feet */
        displayFeet = armorConfig.get("Display Slot Feet", "displayFeet", displayFeet).getBoolean(displayFeet);
        horizontalOffsetFromMiddleFeet = armorConfig.get("Display Slot Feet", "horizontalOffsetFromMiddleFeet", horizontalOffsetFromMiddleFeet).getInt(horizontalOffsetFromMiddleFeet);
        verticalOffsetFromBottomFeet = armorConfig.get("Display Slot Feet", "verticalOffsetFromBottomFeet", verticalOffsetFromBottomFeet).getInt(verticalOffsetFromBottomFeet);
        displayStyleFeet = armorConfig.get("Display Slot Feet", "displayStyleFeet", displayStyleFeet).getInt(displayStyleFeet);
        fontColorFeet = armorConfig.get("Display Slot Feet", "fontColorFeet", fontColorFeet).getInt(fontColorFeet);
        horizontalStringOffsetFeet = armorConfig.get("Display Slot Feet", "horizontalStringOffsetFeet", horizontalStringOffsetFeet).getInt(horizontalStringOffsetFeet);
        verticalStringOffsetFeet = armorConfig.get("Display Slot Feet", "verticalStringOffsetFeet", verticalStringOffsetFeet).getInt(verticalStringOffsetFeet);
        
        /* Render Legs */
        displayLegs = armorConfig.get("Display Slot Legs", "displayLegs", displayLegs).getBoolean(displayLegs);
        horizontalOffsetFromMiddleLegs = armorConfig.get("Display Slot Legs", "horizontalOffsetFromMiddleLegs", horizontalOffsetFromMiddleLegs).getInt(horizontalOffsetFromMiddleLegs);
        verticalOffsetFromBottomLegs = armorConfig.get("Display Slot Legs", "verticalOffsetFromBottomLegs", verticalOffsetFromBottomLegs).getInt(verticalOffsetFromBottomLegs);
        displayStyleLegs = armorConfig.get("Display Slot Legs", "displayStyleLegs", displayStyleLegs).getInt(displayStyleLegs);
        fontColorLegs = armorConfig.get("Display Slot Legs", "fontColorLegs", fontColorLegs).getInt(fontColorLegs);
        horizontalStringOffsetLegs = armorConfig.get("Display Slot Legs", "horizontalStringOffsetLegs", horizontalStringOffsetLegs).getInt(horizontalStringOffsetLegs);
        verticalStringOffsetLegs = armorConfig.get("Display Slot Legs", "verticalStringOffsetLegs", verticalStringOffsetLegs).getInt(verticalStringOffsetLegs);

        /* Render Chest */
        displayChest = armorConfig.get("Display Slot Chest", "displayChest", displayChest).getBoolean(displayChest);
        horizontalOffsetFromMiddleChest = armorConfig.get("Display Slot Chest", "horizontalOffsetFromMiddleChest", horizontalOffsetFromMiddleChest).getInt(horizontalOffsetFromMiddleChest);
        verticalOffsetFromBottomChest = armorConfig.get("Display Slot Chest", "verticalOffsetFromBottomChest", verticalOffsetFromBottomChest).getInt(verticalOffsetFromBottomChest);
        displayStyleChest = armorConfig.get("Display Slot Chest", "displayStyleChest", displayStyleChest).getInt(displayStyleChest);
        fontColorChest = armorConfig.get("Display Slot Chest", "fontColorChest", fontColorChest).getInt(fontColorChest);
        horizontalStringOffsetChest = armorConfig.get("Display Slot Chest", "horizontalStringOffsetChest", horizontalStringOffsetChest).getInt(horizontalStringOffsetChest);
        verticalStringOffsetChest = armorConfig.get("Display Slot Chest", "verticalStringOffsetChest", verticalStringOffsetChest).getInt(verticalStringOffsetChest);

        /* Render Head */
        displayHead = armorConfig.get("Display Slot Head", "displayHead", displayHead).getBoolean(displayHead);
        horizontalOffsetFromMiddleHead = armorConfig.get("Display Slot Head", "horizontalOffsetFromMiddleHead", horizontalOffsetFromMiddleHead).getInt(horizontalOffsetFromMiddleHead);
        verticalOffsetFromBottomHead = armorConfig.get("Display Slot Head", "verticalOffsetFromBottomHead", verticalOffsetFromBottomHead).getInt(verticalOffsetFromBottomHead);
        displayStyleHead = armorConfig.get("Display Slot Head", "displayStyleHead", displayStyleHead).getInt(displayStyleHead);
        fontColorHead = armorConfig.get("Display Slot Head", "fontColorHead", fontColorHead).getInt(fontColorHead);
        horizontalStringOffsetHead = armorConfig.get("Display Slot Head", "horizontalStringOffsetHead", horizontalStringOffsetHead).getInt(horizontalStringOffsetHead);
        verticalStringOffsetHead = armorConfig.get("Display Slot Head", "verticalStringOffsetHead", verticalStringOffsetHead).getInt(verticalStringOffsetHead);

        /* Render Arrow */
        displayArrow = armorConfig.get("Display Slot Arrows", "displayArrow", displayArrow).getBoolean(displayArrow);
        horizontalOffsetFromMiddleArrow = armorConfig.get("Display Slot Arrows", "horizontalOffsetFromMiddleArrow", horizontalOffsetFromMiddleArrow).getInt(horizontalOffsetFromMiddleArrow);
        verticalOffsetFromBottomArrow = armorConfig.get("Display Slot Arrows", "verticalOffsetFromBottomArrow", verticalOffsetFromBottomArrow).getInt(verticalOffsetFromBottomArrow);
        displayStyleArrow = armorConfig.get("Display Slot Arrows", "displayStyleArrow", displayStyleArrow).getInt(displayStyleArrow);
        fontColorArrow = armorConfig.get("Display Slot Arrows", "fontColorArrow", fontColorArrow).getInt(fontColorArrow);
        horizontalStringOffsetArrow = armorConfig.get("Display Slot Arrows", "horizontalStringOffsetArrow", horizontalStringOffsetArrow).getInt(horizontalStringOffsetArrow);
        verticalStringOffsetArrow = armorConfig.get("Display Slot Arrows", "verticalStringOffsetArrow", verticalStringOffsetArrow).getInt(verticalStringOffsetArrow);
        maxOfArrows = armorConfig.get("Display Slot Arrows", "maxOfArrows", maxOfArrows).getInt(maxOfArrows);

        /* Render GenericItem1 */
        displayGenericItem1 = armorConfig.get("Display Slot Optional Item 1", "displayGenericItem1", displayGenericItem1).getBoolean(displayGenericItem1);
        genericItem1ShiftedID = armorConfig.get("Display Slot Optional Item 1", "genericItem1ShiftedID", genericItem1ShiftedID).getInt(genericItem1ShiftedID);
        trackingTypeGenericItem1 = armorConfig.get("Display Slot Optional Item 1", "trackingTypeGenericItem1", trackingTypeGenericItem1).getInt(trackingTypeGenericItem1);

        horizontalOffsetFromMiddleGenericItem1 = armorConfig.get("Display Slot Optional Item 1", "horizontalOffsetFromMiddleGenericItem1", horizontalOffsetFromMiddleGenericItem1).getInt(horizontalOffsetFromMiddleGenericItem1);
        verticalOffsetFromBottomGenericItem1 = armorConfig.get("Display Slot Optional Item 1", "verticalOffsetFromBottomGenericItem1", verticalOffsetFromBottomGenericItem1).getInt(verticalOffsetFromBottomGenericItem1);
        displayStyleGenericItem1 = armorConfig.get("Display Slot Optional Item 1", "displayStyleGenericItem1", displayStyleGenericItem1).getInt(displayStyleGenericItem1);
        fontColorGenericItem1 = armorConfig.get("Display Slot Optional Item 1", "fontColorGenericItem1", fontColorGenericItem1).getInt(fontColorGenericItem1);
        horizontalStringOffsetGenericItem1 = armorConfig.get("Display Slot Optional Item 1", "horizontalStringOffsetGenericItem1", horizontalStringOffsetGenericItem1).getInt(horizontalStringOffsetGenericItem1);
        verticalStringOffsetGenericItem1 = armorConfig.get("Display Slot Optional Item 1", "verticalStringOffsetGenericItem1", verticalStringOffsetGenericItem1).getInt(verticalStringOffsetGenericItem1);
        maxOfGenericItem1 = armorConfig.get("Display Slot Optional Item 1", "maxOfGenericItem1s", maxOfGenericItem1).getInt(maxOfGenericItem1);
        
        /* Render GenericItem2 */
        displayGenericItem2 = armorConfig.get("Display Slot Optional Item 2", "displayGenericItem2", displayGenericItem2).getBoolean(displayGenericItem2);
        genericItem2ShiftedID = armorConfig.get("Display Slot Optional Item 2", "genericItem2ShiftedID", genericItem2ShiftedID).getInt(genericItem2ShiftedID);
        trackingTypeGenericItem2 = armorConfig.get("Display Slot Optional Item 2", "trackingTypeGenericItem2", trackingTypeGenericItem2).getInt(trackingTypeGenericItem2);

        horizontalOffsetFromMiddleGenericItem2 = armorConfig.get("Display Slot Optional Item 2", "horizontalOffsetFromMiddleGenericItem2", horizontalOffsetFromMiddleGenericItem2).getInt(horizontalOffsetFromMiddleGenericItem2);
        verticalOffsetFromBottomGenericItem2 = armorConfig.get("Display Slot Optional Item 2", "verticalOffsetFromBottomGenericItem2", verticalOffsetFromBottomGenericItem2).getInt(verticalOffsetFromBottomGenericItem2);
        displayStyleGenericItem2 = armorConfig.get("Display Slot Optional Item 2", "displayStyleGenericItem2", displayStyleGenericItem2).getInt(displayStyleGenericItem2);
        fontColorGenericItem2 = armorConfig.get("Display Slot Optional Item 2", "fontColorGenericItem2", fontColorGenericItem2).getInt(fontColorGenericItem2);
        horizontalStringOffsetGenericItem2 = armorConfig.get("Display Slot Optional Item 2", "horizontalStringOffsetGenericItem2", horizontalStringOffsetGenericItem2).getInt(horizontalStringOffsetGenericItem2);
        verticalStringOffsetGenericItem2 = armorConfig.get("Display Slot Optional Item 2", "verticalStringOffsetGenericItem2", verticalStringOffsetGenericItem2).getInt(verticalStringOffsetGenericItem2);
        maxOfGenericItem2 = armorConfig.get("Display Slot Optional Item 2", "maxOfGenericItem2s", maxOfGenericItem2).getInt(maxOfGenericItem2);

        /* Render GenericItem3 */
        displayGenericItem3 = armorConfig.get("Display Slot Optional Item 3", "displayGenericItem3", displayGenericItem3).getBoolean(displayGenericItem3);
        genericItem3ShiftedID = armorConfig.get("Display Slot Optional Item 3", "genericItem3ShiftedID", genericItem3ShiftedID).getInt(genericItem3ShiftedID);
        trackingTypeGenericItem3 = armorConfig.get("Display Slot Optional Item 3", "trackingTypeGenericItem3", trackingTypeGenericItem3).getInt(trackingTypeGenericItem3);

        horizontalOffsetFromMiddleGenericItem3 = armorConfig.get("Display Slot Optional Item 3", "horizontalOffsetFromMiddleGenericItem3", horizontalOffsetFromMiddleGenericItem3).getInt(horizontalOffsetFromMiddleGenericItem3);
        verticalOffsetFromBottomGenericItem3 = armorConfig.get("Display Slot Optional Item 3", "verticalOffsetFromBottomGenericItem3", verticalOffsetFromBottomGenericItem3).getInt(verticalOffsetFromBottomGenericItem3);
        displayStyleGenericItem3 = armorConfig.get("Display Slot Optional Item 3", "displayStyleGenericItem3", displayStyleGenericItem3).getInt(displayStyleGenericItem3);
        fontColorGenericItem3 = armorConfig.get("Display Slot Optional Item 3", "fontColorGenericItem3", fontColorGenericItem3).getInt(fontColorGenericItem3);
        horizontalStringOffsetGenericItem3 = armorConfig.get("Display Slot Optional Item 3", "horizontalStringOffsetGenericItem3", horizontalStringOffsetGenericItem3).getInt(horizontalStringOffsetGenericItem3);
        verticalStringOffsetGenericItem3 = armorConfig.get("Display Slot Optional Item 3", "verticalStringOffsetGenericItem3", verticalStringOffsetGenericItem3).getInt(verticalStringOffsetGenericItem3);
        maxOfGenericItem3 = armorConfig.get("Display Slot Optional Item 3", "maxOfGenericItem3s", maxOfGenericItem3).getInt(maxOfGenericItem3);

//        1 = armorConfig.get(Configuration.CATEGORY_GENERAL, "1", Configuration.CATEGORY_GENERAL, 1).getInt(1);
        
        armorConfig.save();
		
		proxy.registerTickers();
	}
	
	@Init
	public void load(FMLInitializationEvent event)
	{
		proxy.registerRenderThings();
	}
	
	@PostInit
	public void postInit(FMLPostInitializationEvent event){

		
	}

	public mod_ArmorBarMod(){
		// TODO Auto-generated constructor stub

	}


}
