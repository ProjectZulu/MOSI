package mosi.display.units;

import java.util.List;

import mosi.Log;
import mosi.display.DisplayHelper;
import mosi.display.DisplayUnitFactory;
import mosi.display.resource.SimpleImageResource.GuiIconImageResource;
import mosi.display.units.DisplayUnit.HorizontalAlignment;
import mosi.display.units.DisplayUnit.VerticalAlignment;
import mosi.display.units.action.ReplaceAction;
import mosi.display.units.windows.DisplayUnitTextBoard;
import mosi.display.units.windows.DisplayUnitTextField;
import mosi.display.units.windows.DisplayUnitTextField.Validator;
import mosi.display.units.windows.DisplayUnitButton;
import mosi.display.units.windows.DisplayUnitToggle;
import mosi.display.units.windows.DisplayWindowMenu;
import mosi.display.units.windows.button.CloseClick;
import mosi.display.units.windows.text.PositionTextValidator;
import mosi.display.units.windows.text.RegularTextValidator;
import mosi.display.units.windows.text.ValidatorBoundedInt;
import mosi.display.units.windows.toggle.ToggleHorizAlign;
import mosi.display.units.windows.toggle.ToggleVertAlign;
import mosi.utilities.Coord;
import mosi.utilities.GsonHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;

import com.google.gson.JsonObject;

public abstract class DisplayUnitPanel extends DisplayUnitMoveable implements DisplayUnit, DisplayUnitSettable {

    // Frequency to determine how often the update loop is run
    private int updateFrequency = 20;
    private DisplayMode displayMode;
    private String nickname;
    private int gridCols; // 0 == Unlimited
    private int gridRows; // 0 == Unlimited
    private Coord gridSpacing = new Coord(18, 24);
    private boolean showEmpty;
    // This is a cache of the previous number of displays that were actually rendered
    private transient int previousDisplaySize = 0;

    private VerticalAlignment vertAlign;
    private HorizontalAlignment horizAlign;

    public enum DisplayMode {
        // Fills entire columns before rows
        COLUMN_GRID,
        // Filles entire rows before columns
        ROW_GRID,
        // Uses the display units own offset for placement
        // TODO: Unimplemented, This should be different class probably parent of this one? Rename dsiplayPanel to
        // DisplayGrid?
        FREE;
        // POSSIBLE FIXED: Provide positions for children? Pephaps seperate display
    }

    public DisplayUnitPanel() {
        super(new Coord(0, 0));
        displayMode = DisplayMode.ROW_GRID;
        gridRows = 3;
        gridCols = 2;
        showEmpty = true;
        vertAlign = VerticalAlignment.CENTER_ABSO;
        horizAlign = HorizontalAlignment.CENTER_ABSO;
        nickname = "Nickname";
    }

    public DisplayUnitPanel(Coord offset, DisplayMode displayMode, int maxCols, int maxRows, boolean showEmpty,
            VerticalAlignment vertAlign, HorizontalAlignment horizAlign) {
        super(offset);
        this.displayMode = displayMode;
        this.gridRows = maxRows;
        this.gridCols = maxCols;
        this.showEmpty = showEmpty;
        this.vertAlign = vertAlign;
        this.horizAlign = horizAlign;
        nickname = "Nickname";
    }

    @Override
    public Coord getSize() {
        switch (displayMode) {
        case COLUMN_GRID:
            if (gridCols == 0) {
                return new Coord(gridCols * gridSpacing.x, gridSpacing.z);
            } else {
                if (gridRows == 0 || gridCols * gridRows < previousDisplaySize) {
                    // -1 because Size is not 0 indexed
                    int displayRow = (int) Math.floor((previousDisplaySize - 1) / gridCols);
                    int displayCol = ((previousDisplaySize - 1) % gridCols);
                    int highestColReached = previousDisplaySize <= gridCols ? (displayCol + 1) : gridCols;
                    return new Coord((displayCol + 1) * gridSpacing.x, highestColReached * gridSpacing.z);
                } else {
                    return new Coord(gridCols * gridSpacing.x, gridRows * gridSpacing.z);
                }
            }
        case ROW_GRID:
            if (gridRows == 0) {
                return new Coord(gridSpacing.x, gridRows * gridSpacing.z);
            } else {
                if (gridCols == 0 || previousDisplaySize < gridCols * gridRows) {
                    // -1 because Size is not 0 indexed
                    int displayRow = ((previousDisplaySize - 1) % gridRows);
                    int displayCol = (int) Math.floor((previousDisplaySize - 1) / gridRows);
                    int highestRowReached = previousDisplaySize <= gridRows ? (displayRow + 1) : gridRows;
                    return new Coord((displayCol + 1) * gridSpacing.x, highestRowReached * gridSpacing.z);
                } else {
                    return new Coord(gridCols * gridSpacing.x, gridRows * gridSpacing.z);
                }
            }
        case FREE:
        default:
            return new Coord(18, 18);
        }
    }

    @Override
    public void setOffset(Coord offset) {
        this.offset = offset;
    }

    @Override
    public void setVerticalAlignment(VerticalAlignment alignment) {
        vertAlign = alignment;
    }

    public void setHorizontalAlignment(HorizontalAlignment alignment) {
        horizAlign = alignment;
    }

    @Override
    public final VerticalAlignment getVerticalAlignment() {
        return vertAlign;
    }

    @Override
    public final HorizontalAlignment getHorizontalAlignment() {
        return horizAlign;
    }

    @Override
    public final void onUpdate(Minecraft mc, int ticks) {
        if (ticks % updateFrequency == 0) {
            update(mc, ticks);
        }
    }

    public abstract void update(Minecraft mc, int ticks);

    @Override
    public boolean shouldRender(Minecraft mc) {
        return true;
    }

    @Override
    public void renderDisplay(Minecraft mc, Coord position) {
        List<? extends DisplayUnit> displayList = getDisplaysToRender();
        switch (displayMode) {
        case FREE:
            Log.log().severe("Panel display mode 'FREE' is not currently implemented. DisplayPanel will not render.");
            break;
        case COLUMN_GRID: {
            int dispIndex = 0; // Current display, independent of list as displays that don't render do not count
            for (DisplayUnit displayUnit : displayList) {
                if (gridCols == 0) {
                    if (displayUnit.shouldRender(mc)) {
                        displayUnit.renderDisplay(mc, position.add(0, gridSpacing.z * dispIndex));
                        dispIndex++;
                    }
                } else {
                    int displayRow = (int) Math.floor(dispIndex / gridCols); // 0-index
                    int displayCol = (dispIndex % gridCols); // 0-index
                    if ((gridRows == 0 || displayRow < gridRows) && displayUnit.shouldRender(mc)) {
                        displayUnit.renderDisplay(mc, position.add(gridSpacing.mult(displayCol, displayRow)));
                        dispIndex++;
                    }
                }
            }
            previousDisplaySize = dispIndex;
            break;
        }
        case ROW_GRID: {
            int dispIndex = 0; // Current display, independent of list as displays that don't render do not count
            for (DisplayUnit displayUnit : displayList) {
                if (gridRows == 0) {
                    if (displayUnit.shouldRender(mc)) {
                        displayUnit.renderDisplay(mc, position.add(gridSpacing.x * dispIndex, 0));
                        dispIndex++;
                    }
                } else {
                    int displayRow = (dispIndex % gridRows);// 0-index
                    int displayCol = (int) Math.floor(dispIndex / gridRows); // 0-index
                    if ((displayCol == 0 || displayCol < gridCols) && displayUnit.shouldRender(mc)) {
                        displayUnit.renderDisplay(mc, position.add(gridSpacing.mult(displayCol, displayRow)));
                        dispIndex++;
                    }
                }
            }
            previousDisplaySize = dispIndex;
            break;
        }
        }
    }

    /**
     * @return List of DisplayUnits should return EMPTY_COLLECTION when no displays, NEVER NULL
     */
    public abstract List<? extends DisplayUnit> getDisplaysToRender();

    @Override
    public void mousePosition(Coord localMouse, HoverAction hoverAction, HoverTracker hoverChecker) {
        if (hoverAction == HoverAction.HOVER) {
            hoverChecker.markHoverFound();
        }
    }

    @Override
    public ActionResult mouseAction(Coord localMouse, MouseAction action, int... actionData) {
        if (action == MouseAction.CLICK && actionData[0] == 1 && DisplayHelper.isCursorOverDisplay(localMouse, this)) {
            DisplayWindowMenu menu = new DisplayWindowMenu(getOffset(), getHorizontalAlignment(),
                    getVerticalAlignment());
            /* Nickname textField */
            menu.addElement(new DisplayUnitTextField(new Coord(0, 4), new Coord(80, 15), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, 13, new RegularTextValidator() {
                        private DisplayUnitPanel display;

                        public RegularTextValidator init(DisplayUnitPanel display) {
                            this.display = display;
                            return this;
                        }

                        @Override
                        public void setString(String text) {
                            display.nickname = text;
                        }

                        @Override
                        public String getString() {
                            return display.nickname;
                        }
                    }.init(this)));
            /* Generic DisplayUnitEditable Settings */
            menu.addElement(new DisplayUnitTextBoard(new Coord(0, 16), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, "Position").setBackgroundImage(null));
            menu.addElement(new DisplayUnitTextField(new Coord(-20, 29), new Coord(40, 15), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, 5, new PositionTextValidator(this, true)));
            menu.addElement(new DisplayUnitTextField(new Coord(+23, 29), new Coord(40, 15), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, 5, new PositionTextValidator(this, false)));

            menu.addElement(new DisplayUnitTextBoard(new Coord(-20, 40), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, "Size").setBackgroundImage(null));
            menu.addElement(new DisplayUnitTextBoard(new Coord(24, 40), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, "Spacing").setBackgroundImage(null));
            menu.addElement(new DisplayUnitTextField(new Coord(-31, 54), new Coord(20, 15), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, 2, new ValidatorBoundedInt(0, 20) {
                        private DisplayUnitPanel panel;

                        public Validator init(DisplayUnitPanel panel) {
                            this.panel = panel;
                            return this;
                        }

                        @Override
                        public void setInt(int textValue) {
                            panel.gridCols = textValue;
                        }

                        public int getValue() {
                            return panel.gridCols;
                        }
                    }.init(this)));
            menu.addElement(new DisplayUnitTextField(new Coord(-10, 54), new Coord(20, 15), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, 2, new ValidatorBoundedInt(0, 20) {
                        private DisplayUnitPanel panel;

                        public Validator init(DisplayUnitPanel panel) {
                            this.panel = panel;
                            return this;
                        }

                        @Override
                        public void setInt(int textValue) {
                            panel.gridRows = textValue;
                        }

                        public int getValue() {
                            return panel.gridRows;
                        }

                    }.init(this)));
            menu.addElement(new DisplayUnitTextField(new Coord(+13, 54), new Coord(20, 15), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, 2, new ValidatorBoundedInt(0, 20) {
                        private DisplayUnitPanel panel;

                        public Validator init(DisplayUnitPanel panel) {
                            this.panel = panel;
                            return this;
                        }

                        @Override
                        public void setInt(int textValue) {
                            panel.gridSpacing = new Coord(textValue, panel.gridSpacing.z);
                        }

                        public int getValue() {
                            return panel.gridSpacing.x;
                        }
                    }.init(this)));
            menu.addElement(new DisplayUnitTextField(new Coord(+34, 54), new Coord(20, 15), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, 2, new ValidatorBoundedInt(0, 20) {
                        private DisplayUnitPanel panel;

                        public Validator init(DisplayUnitPanel panel) {
                            this.panel = panel;
                            return this;
                        }

                        @Override
                        public void setInt(int textValue) {
                            panel.gridSpacing = new Coord(panel.gridSpacing.x, textValue);
                        }

                        public int getValue() {
                            return panel.gridSpacing.z;
                        }
                    }.init(this)));

            menu.addElement(new DisplayUnitTextBoard(new Coord(0, 66), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, "Alignment").setBackgroundImage(null));
            menu.addElement(new DisplayUnitToggle(new Coord(-22, 79), new Coord(20, 20), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, new ToggleHorizAlign(this, HorizontalAlignment.LEFT_ABSO))
                    .setIconImageResource(new GuiIconImageResource(new Coord(111, 2), new Coord(13, 16))));
            menu.addElement(new DisplayUnitToggle(new Coord(+00, 79), new Coord(20, 20), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, new ToggleHorizAlign(this, HorizontalAlignment.CENTER_ABSO))
                    .setIconImageResource(new GuiIconImageResource(new Coord(129, 2), new Coord(13, 16))));
            menu.addElement(new DisplayUnitToggle(new Coord(+22, 79), new Coord(20, 20), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, new ToggleHorizAlign(this, HorizontalAlignment.RIGHT_ABSO))
                    .setIconImageResource(new GuiIconImageResource(new Coord(147, 2), new Coord(13, 16))));
            menu.addElement(new DisplayUnitToggle(new Coord(-22, 100), new Coord(20, 20), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, new ToggleVertAlign(this, VerticalAlignment.TOP_ABSO))
                    .setIconImageResource(new GuiIconImageResource(new Coord(111, 23), new Coord(13, 16))));

            menu.addElement(new DisplayUnitToggle(new Coord(+00, 100), new Coord(20, 20), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, new ToggleVertAlign(this, VerticalAlignment.CENTER_ABSO))
                    .setIconImageResource(new GuiIconImageResource(new Coord(129, 23), new Coord(13, 16))));
            menu.addElement(new DisplayUnitToggle(new Coord(+22, 100), new Coord(20, 20), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, new ToggleVertAlign(this, VerticalAlignment.BOTTOM_ABSO))
                    .setIconImageResource(new GuiIconImageResource(new Coord(147, 23), new Coord(13, 16))));

            menu.addElement(new DisplayUnitButton(new Coord(0, 138), new Coord(80, 15), VerticalAlignment.TOP_ABSO,
                    HorizontalAlignment.CENTER_ABSO, new CloseClick(menu), "Close"));
            menu.addElement(getPanelEditor());

            return new ReplaceAction(menu, true);
        }
        return super.mouseAction(localMouse, action, actionData);
    }

    public abstract DisplayUnit getPanelEditor();

    @Override
    public ActionResult keyTyped(char eventCharacter, int eventKey) {
        return super.keyTyped(eventCharacter, eventKey);
    }

    @Override
    public void saveCustomData(JsonObject jsonObject) {
        jsonObject.addProperty("NICKNAME", nickname);
        super.saveCustomData(jsonObject);
        jsonObject.addProperty("DISPLAYMODE", displayMode.toString());
        jsonObject.addProperty("UPDATE_FREQUENCY", updateFrequency);

        jsonObject.addProperty("GRID_COLS", gridCols);
        jsonObject.addProperty("GRID_ROWS", gridRows);
        jsonObject.addProperty("GRID_SPACING", gridSpacing.x + "," + gridSpacing.z);

        jsonObject.addProperty("VERTICAL_ALIGN", vertAlign.toString());
        jsonObject.addProperty("HORIZONTAL_ALIGN", horizAlign.toString());
    }

    @Override
    public void loadCustomData(DisplayUnitFactory factory, JsonObject customData) {
        super.loadCustomData(factory, customData);
        nickname = GsonHelper.getMemberOrDefault(customData, "NICKNAME", nickname);
        String parsedDisplay = GsonHelper.getMemberOrDefault(customData, "DISPLAYMODE",
                DisplayMode.COLUMN_GRID.toString());
        displayMode = parsedDisplay.trim().equalsIgnoreCase(DisplayMode.COLUMN_GRID.toString()) ? DisplayMode.COLUMN_GRID
                : DisplayMode.ROW_GRID;
        updateFrequency = GsonHelper.getMemberOrDefault(customData, "UPDATE_FREQUENCY", updateFrequency);

        gridCols = GsonHelper.getMemberOrDefault(customData, "GRID_COLS", gridCols);
        gridRows = GsonHelper.getMemberOrDefault(customData, "GRID_ROWS", gridRows);
        gridSpacing = parseCoord(GsonHelper.getMemberOrDefault(customData, "GRID_SPACING", "18, 24"), new Coord(18, 24));

        String verAl = GsonHelper.getMemberOrDefault(customData, "VERTICAL_ALIGN", "").trim();
        for (VerticalAlignment verticalAlignment : VerticalAlignment.values()) {
            if (verAl.trim().toUpperCase().equals(verticalAlignment.toString())) {
                vertAlign = verticalAlignment;
            }
        }

        String horAl = GsonHelper.getMemberOrDefault(customData, "HORIZONTAL_ALIGN", "").trim();
        for (HorizontalAlignment horizontalAlignment : HorizontalAlignment.values()) {
            if (horAl.trim().toUpperCase().equals(horizontalAlignment.toString())) {
                horizAlign = horizontalAlignment;
            }
        }
    }

    private Coord parseCoord(String stringForm, Coord defaultCoord) {
        String[] parts = stringForm.split(",");
        if (parts.length == 2) {
            try {
                int xCoord = Integer.parseInt(parts[0]);
                int zCoord = Integer.parseInt(parts[1]);
                return new Coord(xCoord, zCoord);
            } catch (NumberFormatException e) {
                Log.log().info("Error parsing coordinate string %s. Will be replaced by %s", stringForm, defaultCoord);
            }
        }
        return defaultCoord;
    }
}
