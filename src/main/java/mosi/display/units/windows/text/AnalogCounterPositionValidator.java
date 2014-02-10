package mosi.display.units.windows.text;

import mosi.display.units.DisplayUnitCountable;
import mosi.display.units.DisplayUnitSettable;
import mosi.display.units.windows.DisplayUnitTextField.Validator;
import mosi.utilities.Coord;
import mosi.utilities.StringHelper;
import net.minecraft.util.ChatAllowedCharacters;

public class AnalogCounterPositionValidator implements Validator {
    private DisplayUnitCountable display;
    private boolean xCoord;

    public AnalogCounterPositionValidator(DisplayUnitCountable settableDisplay, boolean xCoord) {
        this.display = settableDisplay;
        this.xCoord = xCoord;
    }

    @Override
    public boolean isCharacterValid(char eventCharacter) {
        return ('-' == eventCharacter || Character.isDigit(eventCharacter))
                && ChatAllowedCharacters.isAllowedCharacter(eventCharacter);
    }

    @Override
    public boolean isStringValid(String text) {
        return StringHelper.isInteger(text);
    }

    @Override
    public void setString(String text) {
        if (xCoord) {
            display.setAnalogOffset(new Coord(Integer.parseInt(text), display.getAnalogOffset().z));
        } else {
            display.setAnalogOffset(new Coord(display.getAnalogOffset().x, Integer.parseInt(text)));
        }
    }

    @Override
    public String getString() {
        if (xCoord) {
            return Integer.toString(display.getAnalogOffset().x);
        } else {
            return Integer.toString(display.getAnalogOffset().z);
        }
    }
}
