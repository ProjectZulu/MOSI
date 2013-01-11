package armorbarmod.common;

public enum Setting {
	/* Should Test Be Offset to Right or Left 
	 * Mutually Exclusive, if Both Declared one will override the other */
	FlowRight,
	FlowLeft,
	
	/* Should Display Analog and/or Digital Bar */
	AnalogBar,
	DigitalCounter,
	
	/* Whether This should Display When Empty
	 * Mutually Exclusive, if Both Declared one will override the other */
	HideWhenEmpty,
	DisplayWhenEmpty,
	
	/* Whether Should Track Durability or the Amount Present
	 * Mutually Exclusive, if Both Declared one will override the other */
	trackDurability,
	trackAmount,
	
	/* Analog Maximum representing Full Bar */
	FullStack,
	
	/* Whether the item damage should be considered for matching when tracking item */
	equalItemMeta,
}
