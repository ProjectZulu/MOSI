package mosi.display.units.windows.text;

import mosi.display.units.windows.DisplayUnitTextField.Validator;
import mosi.utilities.StringHelper;
import net.minecraft.util.ChatAllowedCharacters;

public abstract class ValidatorBoundedInt implements Validator {
    private int lowerBound;
    private int upperBound;

    public ValidatorBoundedInt(int lowerBound, int upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public boolean isCharacterValid(char eventCharacter) {
        return ('-' == eventCharacter || Character.isDigit(eventCharacter))
                && ChatAllowedCharacters.isAllowedCharacter(eventCharacter);
    }

    @Override
    public boolean isStringValid(String text) {
        if (!StringHelper.isInteger(text)) {
            return false;
        }
        int number = Integer.parseInt(text);
        return number >= lowerBound && number <= upperBound;
    }
}
