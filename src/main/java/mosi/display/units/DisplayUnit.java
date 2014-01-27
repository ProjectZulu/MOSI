package mosi.display.units;

import mosi.display.DisplayUnitFactory;
import mosi.utilities.Coord;
import net.minecraft.client.Minecraft;

import com.google.common.base.Optional;
import com.google.gson.JsonObject;

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

    public void mouseMove(int mouseLocalX, int mouseLocalY);

    public enum MouseAction {
        /* vararg 0: int EventButton */
        CLICK,
        /* vararg 0: int lastButtonClicked */
        CLICK_MOVE,
        /* No arguments */
        RELEASE;
    }

    public static class ActionResult {
        public final boolean stopActing;
        public final Optional<DisplayWindow> display;

        public ActionResult(boolean stopActing) {
            this.stopActing = stopActing;
            display = Optional.absent();
        }

        public ActionResult(boolean stopActing, DisplayWindow dispaly) {
            this.stopActing = stopActing;
            display = dispaly != null ? Optional.of(dispaly) : Optional.<DisplayWindow> absent();
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
