package armorbarmod.common;

import org.lwjgl.util.Point;

import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;

public enum DisplayBuilder {
	Feet {
		@Override
		void loadDisplayUnit(Configuration config) {
			displayUnit = new DisplayUnitArmorSlot(this.toString(), 0, true, 1030655, new Point(95, (16*0+4)+16), new Point(0, 16), new Point(16, 4-8));
		}
	},
	Legs{
		@Override
		void loadDisplayUnit(Configuration config) {
			displayUnit = new DisplayUnitArmorSlot(this.toString(), 1, true, 1030655, new Point(95, (16*1+4+2)+16), new Point(0, 16), new Point(16, 4-8));
		}
	},
	Chest{
		@Override
		void loadDisplayUnit(Configuration config) {
			displayUnit = new DisplayUnitArmorSlot(this.toString(), 2, true, 1030655, new Point(-111, (16*0+4)+16), new Point(0, 16), new Point(-16, 4-8));
		}
	},
	Head{
		@Override
		void loadDisplayUnit(Configuration config) {
			displayUnit = new DisplayUnitArmorSlot(this.toString(), 3, true, 1030655,
					new Point(-111, (16*1+4+2)+16), new Point(0, 16), new Point(-16, 4-8));
		}
	},
	Arrow{
		@Override
		void loadDisplayUnit(Configuration config) {
			displayUnit = new DisplayUnitTrackItem(this.toString(), Item.arrow.itemID, true, 1030655, 
					new Point(-111-32-16, (16*0+4)+16), new Point(0, 16), new Point(-16, 4-8));
		}
	},
	OtherItem1{
		@Override
		void loadDisplayUnit(Configuration config) {
			displayUnit = (new DisplayUnitTrackItem(this.toString(), Item.swordWood.itemID, true, 1030655, 
					new Point(-111-32-16, (16*1+4)+16), new Point(0, 16), new Point(-16, 4-8))).devTestingMethod();
		}
	},
	OtherItem2{
		@Override
		void loadDisplayUnit(Configuration config) {
			displayUnit = (new DisplayUnitTrackItem(this.toString(), Item.swordWood.itemID, true, 1030655, 
					new Point(-111-32-16, (16*2+4)+16), new Point(0, 16), new Point(-16, 4-8)));
		}
	}
	;
	
	abstract void loadDisplayUnit(Configuration config);
	protected DisplayUnit displayUnit;
	
	public static void loadDisplayFromConfig(Configuration config){
		for (DisplayBuilder displayUnit : DisplayBuilder.values()){
			displayUnit.loadDisplayUnit(config);
		}
	}
	
	public static void buildDisplay(){
		for (DisplayBuilder displayUnit : DisplayBuilder.values()){
			ArmorBarDisplayTicker.displayList.add(displayUnit.displayUnit);
		}
	}
}
