package mosi.display.units;

import mosi.display.DisplayUnitFactory;
import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;

import com.google.common.base.Optional;
import com.google.gson.JsonObject;

/**
 * Base Interface for all display
 * 
 * TODO technically it seems not all of these are required for all display, Displayunit and Displaywindow extending from
 * a common interface DisplayBase may be better encapsulation/OOP. To be revisited once more test cases are established.
 */
public interface DisplayUnit {
    /**
     * String type registered to Class object. IMPORTANT: Type is used for Deserialization
     */
    public abstract String getType();

    public abstract Coord getOffset();

    public abstract Coord getSize();

    public enum VerticalAlignment {
        TOP_PECR, TOP_ABSO, BOTTOM_PERC, BOTTOM_ABSO, CENTER_PERC, CENTER_ABSO;
    }

    public abstract VerticalAlignment getVerticalAlignment();

    public enum HorizontalAlignment {
        LEFT_PERC, LEFT_ABSO, RIGHT_PERC, RIGHT_ABSO, CENTER_PERC, CENTER_ABSO;
    }

    public abstract HorizontalAlignment getHorizontalAlignment();

    public void onUpdate(Minecraft mc, int ticks);

    public boolean shouldRender(Minecraft mc);

    public void renderDisplay(Minecraft mc, Coord Position);

    public void mousePosition(Coord localMouse);

    public enum MouseAction {
        /* vararg 0: int EventButton */
        CLICK,
        /* vararg 0: int lastButtonClicked */
        CLICK_MOVE,
        /* No arguments */
        RELEASE;
    }

    public static class ActionResult {
        /* i.e. This action occured on KeyPress.Y, no other Displays should receive the keypress event. */
        public final boolean stopActing;
        /* Whether display should be set as the active window */
        public final INTERACTION interaction;
        public final Optional<DisplayWindow> display;

        /**
         * Provides the parent/containter of this DisplayUnit knowleadge of how to react to the ActionResult Note that
         * in all cases, open can only occur on non-null instances
         */
        public enum INTERACTION {
            /* Do nothing, nado, zero, ziltch. display instance will be ignored */
            NONE,
            /* add, in addition to other displays if supported */
            OPEN,
            /* CLOSE provided displays instance if present */
            CLOSE,
            /* CLOSE the ActionResult provider instance and add provided, if supported */
            REPLACE,
            /* clsoe ALL OTHER displays on the current level and add provided instance if not null */
            REPLACE_ALL;
        }

        public ActionResult(boolean stopActing) {
            this.stopActing = stopActing;
            this.interaction = INTERACTION.NONE;
            this.display = Optional.absent();
        }

        public ActionResult(boolean stopActing, INTERACTION interaction, DisplayWindow display) {
            this.stopActing = stopActing;
            this.interaction = interaction;
            this.display = Optional.of(display);
        }

        public static class NoAction extends ActionResult {
            public NoAction() {
                super(false);
            }
        }
    }

    public ActionResult mouseAction(Coord localMouse, MouseAction action, int... actionData);

    public ActionResult keyTyped(char eventCharacter, int eventKey);

    public abstract JsonObject saveCustomData(JsonObject jsonObject);

    public abstract void loadCustomData(DisplayUnitFactory factory, JsonObject customData);
}
