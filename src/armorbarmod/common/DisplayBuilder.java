package armorbarmod.common;

import java.util.EnumSet;

import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;

import org.lwjgl.util.Point;

public enum DisplayBuilder {
	Feet {
		@Override
		void createDisplayUnit() {
			displayUnit = new DisplayUnitArmorSlot(this.toString(), 0, true, 1030655, new Point(95, (16*0+4)+16));
			displayUnit.loadProfile(EnumSet.of(Setting.FlowRight, Setting.AnalogBar, Setting.DisplayWhenEmpty));
		}
	},
	Legs{
		@Override
		void createDisplayUnit() {
			displayUnit = new DisplayUnitArmorSlot(this.toString(), 1, true, 1030655, new Point(95, (16*1+4+2)+16));
			displayUnit.loadProfile(EnumSet.of(Setting.FlowRight, Setting.AnalogBar, Setting.DisplayWhenEmpty));
		}
	},
	Chest{
		@Override
		void createDisplayUnit() {
			displayUnit = new DisplayUnitArmorSlot(this.toString(), 2, true, 1030655, new Point(-111, (16*0+4)+16));
			displayUnit.loadProfile(EnumSet.of(Setting.FlowLeft, Setting.AnalogBar, Setting.DisplayWhenEmpty));
		}
	},
	Head{
		@Override
		void createDisplayUnit() {
			displayUnit = new DisplayUnitArmorSlot(this.toString(), 3, true, 1030655, new Point(-111, (16*1+4+2)+16));
			displayUnit.loadProfile(EnumSet.of(Setting.FlowLeft, Setting.AnalogBar, Setting.DisplayWhenEmpty));
		}
	},
	Arrow{
		@Override
		void createDisplayUnit() {
			displayUnit = new DisplayUnitTrackItem(this.toString(), Item.arrow.itemID, true, 1030655,  new Point(-111-32-16, (16+4)*0+20));
			displayUnit.loadProfile(EnumSet.of(Setting.FlowRight, Setting.AnalogBar, Setting.DigitalCounter, Setting.DisplayWhenEmpty, Setting.trackAmount));
		}
	},
	GenericDurabilityCounter1{
		@Override
		void createDisplayUnit() {
			displayUnit = new DisplayUnitTrackItem(this.toString(), Item.swordWood.itemID, true, 1030655, new Point(-111-32-16, (16+4)*1+20));
			displayUnit.loadProfile(EnumSet.of(Setting.FlowRight, Setting.AnalogBar, Setting.DisplayWhenEmpty, Setting.trackDurability));
		}
	},
	GenericDurabilityCounter2{
		@Override
		void createDisplayUnit() {
			displayUnit = new DisplayUnitTrackItem(this.toString(), Item.swordSteel.itemID, true, 1030655, new Point(-111-32-16, (16+4)*2+20));
			displayUnit.loadProfile(EnumSet.of(Setting.FlowRight, Setting.AnalogBar, Setting.DisplayWhenEmpty, Setting.trackDurability));
		}
	},
	GenericAmountCounter1{
		@Override
		void createDisplayUnit() {
			displayUnit = new DisplayUnitTrackItem(this.toString(), Item.coal.itemID, true, 1030655, new Point(-111-32-16, (16+4)*3+20));
			displayUnit.loadProfile(EnumSet.of(Setting.FlowRight, Setting.AnalogBar, Setting.DigitalCounter, Setting.DisplayWhenEmpty, Setting.trackAmount, 
					Setting.equalItemMeta));
		}
	},
	GenericAmountCounter2{
		@Override
		void createDisplayUnit() {
			displayUnit = new DisplayUnitTrackItem(this.toString(), Item.diamond.itemID, true, 1030655, new Point(-111-32-16, (16+4)*4+20));
			displayUnit.loadProfile(EnumSet.of(Setting.FlowRight, Setting.AnalogBar, Setting.DigitalCounter, Setting.DisplayWhenEmpty, Setting.trackAmount, 
					Setting.equalItemMeta));
		}
	};
	
	abstract void createDisplayUnit();
	protected DisplayUnit displayUnit;
	
	public static void loadDisplayFromConfig(Configuration config){
		for (DisplayBuilder displayBuilderUnit : DisplayBuilder.values()){
			displayBuilderUnit.createDisplayUnit();
			displayBuilderUnit.displayUnit.getFromConfig(config);
		}
	}
	
	public static void buildDisplay(){
		for (DisplayBuilder displayBuilderUnit : DisplayBuilder.values()){
			MOSIDisplayTicker.displayList.add(displayBuilderUnit.displayUnit);
		}
	}
}
