package mosi.display.units.windows.text;

import net.minecraft.util.ChatAllowedCharacters;
import mosi.display.units.windows.DisplayUnitTextField.Validator;

public abstract class RegularTextValidator implements Validator {

    @Override
    public boolean isCharacterValid(char eventCharacter) {
        return ChatAllowedCharacters.isAllowedCharacter(eventCharacter);
    }

    @Override
    public boolean isStringValid(String text) {
        for (char character : text.toCharArray()) {
            if (!isCharacterValid(character)) {
                return false;
            }
        }
        return true;
    }
}
