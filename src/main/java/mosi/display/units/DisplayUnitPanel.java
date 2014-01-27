package mosi.display.units;

import java.util.ArrayList;
import java.util.List;

import mosi.Log;
import mosi.display.DisplayUnitFactory;
import mosi.display.units.DisplayUnit.HorizontalAlignment;
import mosi.display.units.DisplayUnit.MouseAction;
import mosi.display.units.DisplayUnit.VerticalAlignment;
import mosi.display.units.DisplayUnit.ActionResult.NoAction;
import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import com.google.common.base.Optional;
import com.google.gson.JsonObject;

public abstract class DisplayUnitPanel implements DisplayUnit {

    // Frequency to determine how often the update loop is run
    private int updateFrequency = 20;
    private DisplayMode displayMode;

    private int gridCols; // 0 == Unlimited
    private int gridRows; // 0 == Unlimited
    private Coord gridSpacing = new Coord(18, 24);
    private boolean showEmpty;
    // This is a cache of the previous number of displays that were actually rendered
    private transient int previousDisplaySize = 0;

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
        displayMode = DisplayMode.ROW_GRID;
        gridRows = 3;
        gridCols = 2;
        showEmpty = true;
    }

    public DisplayUnitPanel(DisplayMode displayMode, int maxCols, int maxRows, boolean showEmpty) {
        this.displayMode = displayMode;
        this.gridRows = maxRows;
        this.gridCols = maxCols;
        this.showEmpty = showEmpty;
    }

    @Override
    public final Coord getOffset() {
        return new Coord(0, 0);
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
    public final VerticalAlignment getVerticalAlignment() {
        return VerticalAlignment.CENTER_ABSO;
    }

    @Override
    public final HorizontalAlignment getHorizontalAlignment() {
        return HorizontalAlignment.CENTER_ABSO;
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
    public void mouseMove(int mouseLocalX, int mouseLocalY) {
    }

    @Override
    public ActionResult mouseAction(Coord localMouse, MouseAction action, int... actionData) {
        return new NoAction();
    }

    @Override
    public ActionResult keyTyped(char eventCharacter, int eventKey) {
        return new NoAction();
    }

    @Override
    public JsonObject saveCustomData(JsonObject jsonObject) {
        return null;
    }

    @Override
    public void loadCustomData(DisplayUnitFactory factory, JsonObject customData) {

    }
}
