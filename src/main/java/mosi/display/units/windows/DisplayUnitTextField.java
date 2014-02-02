package mosi.display.units.windows;

import mosi.Log;
import mosi.display.DisplayHelper;
import mosi.display.DisplayRenderHelper;
import mosi.display.DisplayUnitFactory;
import mosi.display.units.DisplayUnit;
import mosi.display.units.DisplayUnit.ActionResult.SimpleAction;
import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;

/**
 * Basic text field with wrapping text
 */
public class DisplayUnitTextField implements DisplayUnit {
    public static final ResourceLocation guiButton = new ResourceLocation("mosi", "buttongui.png");

    public static final String DISPLAY_ID = "DisplayUnitTextField";

    private Coord offset;
    private Coord size;
    private VerticalAlignment vertAlign;
    private HorizontalAlignment horizAlign;
    private boolean isSelected = false;
    /** Text that will need to be displayed, shouldn't be set directly use {@link #setText() or {@link #writeText()} */
    private String displayText;
    private Validator textValidator;

    /** The current character index that should be used as start of the rendered text. */
    private int lineScrollOffset;
    /** opposite end of cursorPosition when selecting multiple character, is otherwise identical to cursorPosition */
    private int cursorPosition;
    /** opposite end of cursorPosition when selecting multiple character, is otherwise identical to cursorPosition */
    private int selectionEnd;
    /** Maximum length allowed for displayText */
    private int maxStringLength;

    public static interface Validator {
        /* Validate if the character would be a valid addition */
        public abstract boolean isCharacterValid(int characterKey);

        /* Validate if the entire string is valid, may be used on occasion to reset the text */
        public abstract boolean isStringValid(String text);

        /* Set text to base display */
        public abstract void setString(String text);

        /* Get string from client, used for initial value */
        public abstract String getString(String text);
    }

    public DisplayUnitTextField(Coord offset, Coord size, VerticalAlignment vertAlign, HorizontalAlignment horizAlign) {
        this.offset = offset;
        this.size = size;
        this.vertAlign = vertAlign;
        this.horizAlign = horizAlign;
        setText("TEST");
        setCursorPosition(displayText.length());
        maxStringLength = 10;
    }

    private FontRenderer getFontRenderer() {
        return Minecraft.getMinecraft().fontRenderer;
    }

    /** Replaces the text wholsesale with minor checks for validity */
    public void setText(String text) {
        // TODO textValidator.isStringValid(text);
        if (text.length() > this.maxStringLength) {
            displayText = text.substring(0, this.maxStringLength);
        } else {
            displayText = text;
        }
        setCursorPositionEnd();
    }

    /**
     * Handles appending text, replacing selection, and then moving cursor. To replace text wholesale use
     * {@link #setText()}
     */
    public void writeText(String addition) {
        addition = ChatAllowedCharacters.filerAllowedCharacters(addition);
        // Log.log().info("BLah %s, {%s, %s}", addition, cursorPosition, selectionEnd);
        int i = cursorPosition < selectionEnd ? cursorPosition : selectionEnd;
        int j = cursorPosition < selectionEnd ? selectionEnd : cursorPosition;
        int k = maxStringLength - displayText.length() - (i - selectionEnd);
        boolean flag = false;
        Log.log().info("BLah %s, %s, %s", i, j, k);

        String result = "";
        Log.log().info("BLah %s", result);
        if (displayText.length() > 0) {
            result = result + displayText.substring(0, i);
        }

        int l;

        if (k < addition.length()) {
            result = result + addition.substring(0, k);
            l = k;
        } else {
            result = result + addition;
            l = addition.length();
        }

        if (displayText.length() > 0 && j < displayText.length()) {
            result = result + displayText.substring(j);
        }

        displayText = result;
        moveCursorBy(i - selectionEnd + l);
    }

    /**
     * Deletes the specified number of words starting at the cursor position. Negative numbers will delete words left of
     * the cursor.
     */
    public void deleteWords(int par1) {
        if (displayText.length() != 0) {
            if (selectionEnd != cursorPosition) {
                writeText("");
            } else {
                deleteFromCursor(getNthWordFromCursor(par1) - cursorPosition);
            }
        }
    }

    /**
     * delete the selected text, otherwsie deletes characters from either side of the cursor. params: delete num
     */
    public void deleteFromCursor(int par1) {
        if (displayText.length() != 0) {
            if (selectionEnd != cursorPosition) {
                writeText("");
            } else {
                boolean flag = par1 < 0;
                int j = flag ? cursorPosition + par1 : cursorPosition;
                int k = flag ? cursorPosition : cursorPosition + par1;
                String s = "";

                if (j >= 0) {
                    s = displayText.substring(0, j);
                }

                if (k < displayText.length()) {
                    s = s + displayText.substring(k);
                }

                displayText = s;

                if (flag) {
                    this.moveCursorBy(par1);
                }
            }
        }
    }

    /**
     * see @getNthNextWordFromPos() params: N, position
     */
    @Deprecated
    private int getNthWordFromCursor(int wordOffsetToGet) {
        return this.getNthWordFromPos(wordOffsetToGet, cursorPosition);
    }

    /**
     * gets the position of the nth word. N may be negative, then it looks backwards. params: N, position
     */
    private int getNthWordFromPos(int wordOffsetToGet, int position) {
        return this.getNthWordFromPos(wordOffsetToGet, position, true);
    }

    private int getNthWordFromPos(int wordOffsetToGet, int startPosition, boolean par3) {
        int position = startPosition;
        boolean searchBackwards = wordOffsetToGet < 0;
        int wordNum = Math.abs(wordOffsetToGet);

        for (int i = 0; i < wordNum; ++i) {
            if (searchBackwards) {
                while (par3 && position > 0 && displayText.charAt(position - 1) == 32) {
                    --position;
                }

                while (position > 0 && displayText.charAt(position - 1) != 32) {
                    --position;
                }
            } else {
                int maxPosition = displayText.length();
                position = displayText.indexOf(32, position);

                if (position == -1) {
                    position = maxPosition;
                } else {
                    while (par3 && position < maxPosition && displayText.charAt(position) == 32) {
                        ++position;
                    }
                }
            }
        }
        return position;
    }

    protected void setCursorPosition(int par1) {
        this.cursorPosition = par1;
        int j = this.displayText.length();

        if (this.cursorPosition < 0) {
            this.cursorPosition = 0;
        }

        if (this.cursorPosition > j) {
            this.cursorPosition = j;
        }

        this.setSelectionPos(this.cursorPosition);
    }

    private void setCursorPositionEnd() {
        setCursorPosition(displayText.length());
    }

    public void moveCursorBy(int par1) {
        setCursorPosition(selectionEnd + par1);
    }

    /** Sets the position of the selection cursor and recalculates lineScrollOffset */
    public void setSelectionPos(int pos) {
        int maxPos = displayText.length();
        pos = pos > maxPos ? maxPos : pos < 0 ? 0 : pos;
        selectionEnd = pos;

        FontRenderer fontRenderer = getFontRenderer();

        if (fontRenderer != null) {
            if (lineScrollOffset > maxPos) {
                lineScrollOffset = maxPos;
            }

            int width = getSize().x;
            String trimDisplay = fontRenderer.trimStringToWidth(displayText.substring(lineScrollOffset), width);
            int maxPosToFit = trimDisplay.length() + lineScrollOffset;

            /* Calculate desired lineScrollOffset such that selected position is last character displayed. */
            if (pos == lineScrollOffset) {
                lineScrollOffset -= fontRenderer.trimStringToWidth(displayText, width, true).length();
            }

            if (pos > maxPosToFit) {
                lineScrollOffset += pos - maxPosToFit;
            } else if (pos <= lineScrollOffset) {
                lineScrollOffset -= lineScrollOffset - pos;
            }

            if (lineScrollOffset < 0) {
                lineScrollOffset = 0;
            }

            if (lineScrollOffset > maxPos) {
                lineScrollOffset = maxPos;
            }
        }
    }

    public String getSelectedtext() {
        int startIndex = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int endIndex = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        return displayText.substring(startIndex, endIndex);
    }

    @Override
    public String getType() {
        return DISPLAY_ID;
    }

    @Override
    public Coord getOffset() {
        return offset;
    }

    @Override
    public Coord getSize() {
        return size;
    }

    @Override
    public VerticalAlignment getVerticalAlignment() {
        return vertAlign;
    }

    @Override
    public HorizontalAlignment getHorizontalAlignment() {
        return horizAlign;
    }

    @Override
    public void onUpdate(Minecraft mc, int ticks) {

    }

    @Override
    public boolean shouldRender(Minecraft mc) {
        return true;
    }

    @Override
    public void renderDisplay(Minecraft mc, Coord position) {
        FontRenderer fontRenderer = mc.fontRenderer;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.func_148821_a(770, 771, 1, 0);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        // DisplayRenderHelper.drawTexturedModalRect(Tessellator.instance, 1.0f, position.x + 3, position.z + 2, 111, 2,
        // 12, 16);
        mc.getTextureManager().bindTexture(guiButton);

        /* Background */
        // TODO: The Background Texture and Coords for Toggled/UnToggled/Hover need to be configurable via a setter, BUT
        // the default is set during the constructor
        // if (toggle.isToggled()) {
        // DisplayRenderHelper.drawTexture4Quadrants(Tessellator.instance, -1.0f, position, getSize(), new Coord(129,
        // 129), new Coord(127, 127));
        // } else
        if (isSelected) {
            DisplayRenderHelper.drawTexture4Quadrants(Tessellator.instance, -1.0f, position, getSize(), new Coord(000,
                    0), new Coord(127, 127));
        } else {
            DisplayRenderHelper.drawTexture4Quadrants(Tessellator.instance, -0.1f, position, getSize(), new Coord(129,
                    0), new Coord(127, 127));
        }

        String shortName = (String) fontRenderer.listFormattedStringToWidth(displayText, getSize().x).get(0);
        // Note posZ-4+getSize/2. -4 is to 'center' the string vertically, and getSize/2 is to move center to the
        // middle button
        DisplayRenderHelper.drawCenteredString(fontRenderer, shortName, position.x + 1 + getSize().x / 2, position.z
                - 4 + getSize().z / 2, 16777120, true);
    }

    @Override
    public SimpleAction mousePosition(Coord localMouse) {
        return ActionResult.NOACTION;
    }

    @Override
    public ActionResult mouseAction(Coord localMouse, MouseAction action, int... actionData) {
        if (DisplayHelper.isCursorOverDisplay(localMouse, this)) {
            if (action == MouseAction.CLICK && actionData[0] == 0) {
                isSelected = true;
                return ActionResult.SIMPLEACTION;
            }
        } else {
            isSelected = false;
            return ActionResult.NOACTION;
        }
        return ActionResult.NOACTION;
    }

    // See func_146201_a in guiTextField
    @Override
    public ActionResult keyTyped(char eventCharacter, int eventKey) {
        // TODO: Esc key should be bound to escape text selection
        if (isSelected) {
            // displayText = "=" + Character.getNumericValue(eventCharacter);
            ActionResult result = textboxKeyTyped(eventCharacter, eventKey) ? ActionResult.SIMPLEACTION
                    : ActionResult.NOACTION;
            // Log.log().info(displayText);
            return result;
        } else {
            return ActionResult.NOACTION;
        }
    }

    private boolean textboxKeyTyped(char eventCharacter, int eventKey) {
        if (isSelected) {
            switch (eventCharacter) {
            case 1:
                this.setCursorPositionEnd();
                this.setSelectionPos(0);
                return true;
            case 3:// Copy, CTRL + C
                DisplayHelper.setClipboardString(getSelectedtext());
                return true;
            case 22:// Paste, CTRL + V
                writeText(DisplayHelper.getClipboardString());
                return true;
            case 24: // Cut, CTRL + X
                DisplayHelper.setClipboardString(getSelectedtext());
                writeText("");
                return true;
            default:
                switch (eventKey) {
                case 14: // Ctrl + backspace = DeleteLastWord
                    if (DisplayHelper.isCtrlKeyDown()) {
                        deleteWords(-1);
                    } else {
                        deleteFromCursor(-1);
                    }
                    return true;
                case 199:
                    if (DisplayHelper.isShiftKeyDown()) {
                        setSelectionPos(0);
                    } else {
                        setCursorPosition(0);
                    }
                    return true;
                case 203:
                    if (DisplayHelper.isShiftKeyDown()) {
                        if (DisplayHelper.isCtrlKeyDown()) {
                            setSelectionPos(getNthWordFromPos(-1, selectionEnd));
                        } else {
                            setSelectionPos(selectionEnd - 1);
                        }
                    } else if (DisplayHelper.isCtrlKeyDown()) {
                        setCursorPosition(getNthWordFromCursor(-1));
                    } else {
                        moveCursorBy(-1);
                    }
                    return true;
                case 205:
                    if (DisplayHelper.isShiftKeyDown()) {
                        if (DisplayHelper.isCtrlKeyDown()) {
                            setSelectionPos(getNthWordFromPos(1, selectionEnd));
                        } else {
                            setSelectionPos(selectionEnd + 1);
                        }
                    } else if (DisplayHelper.isCtrlKeyDown()) {
                        setCursorPosition(getNthWordFromCursor(1));
                    } else {
                        moveCursorBy(1);
                    }
                    return true;
                case 207:
                    if (DisplayHelper.isShiftKeyDown()) {
                        setSelectionPos(displayText.length());
                    } else {
                        setCursorPositionEnd();
                    }
                    return true;
                case 211:
                    if (DisplayHelper.isCtrlKeyDown()) {
                        deleteWords(1);
                    } else {
                        deleteFromCursor(1);
                    }
                    return true;
                default:
                    if (ChatAllowedCharacters.isAllowedCharacter(eventCharacter)) {
                        writeText(Character.toString(eventCharacter));
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
    }

    @Override
    public JsonObject saveCustomData(JsonObject jsonObject) {
        return null;
    }

    @Override
    public void loadCustomData(DisplayUnitFactory factory, JsonObject customData) {
    }
}
