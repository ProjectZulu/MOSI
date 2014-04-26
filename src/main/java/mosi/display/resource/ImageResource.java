package mosi.display.resource;

import mosi.utilities.Coord;
import net.minecraft.util.ResourceLocation;

public interface ImageResource {

    public abstract ResourceLocation getImageToBind();

    public abstract Coord getImageUV();

    public abstract Coord getImageSize();
}
