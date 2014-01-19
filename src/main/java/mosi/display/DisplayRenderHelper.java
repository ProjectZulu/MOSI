package mosi.display;

import org.lwjgl.util.Point;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;

public class DisplayRenderHelper {
    /**
     * Fundamental Minecraft Function to Draw a Texture in the World. Copied from Minecraft Code, not sure where. It is
     * literally identical in several places in MC code.
     */
    public static void drawTexturedModalRect(Tessellator tess, float zLevel, int par1, int par2, int par3, int par4,
            int par5, int par6) {
        float var7 = 0.00390625F;
        float var8 = 0.00390625F;
        tess.startDrawingQuads();
        // tes.addVertexWithUV(x1, y2, z, u1, v2);
        // tes.addVertexWithUV(x2, y2, z, u2, v2);
        // tes.addVertexWithUV(x2, y1, z, u2, v1);
        // tes.addVertexWithUV(x1, y1, z, u1, v1);
        tess.addVertexWithUV((double) (par1 + 0), (double) (par2 + par6), zLevel, ((float) (par3 + 0) * var7), ((float) (par4 + par6) * var8));
        tess.addVertexWithUV((double) (par1 + par5), (double) (par2 + par6), zLevel, ((float) (par3 + par5) * var7), ((float) (par4 + par6) * var8));
        tess.addVertexWithUV((double) (par1 + par5), (double) (par2 + 0), zLevel, ((float) (par3 + par5) * var7), ((float) (par4 + 0) * var8));
        tess.addVertexWithUV((double) (par1 + 0), (double) (par2 + 0), zLevel, ((float) (par3 + 0) * var7), ((float) (par4 + 0) * var8));
        tess.draw();
    }
    
    public static void drawTextureModelFromIcon(Tessellator tess, Icon icon, Point screenPosition) {
        final float minU = icon.getMinU();
        final float maxU = icon.getMaxU();
        final float minV = icon.getMinV();
        final float maxV = icon.getMaxV();
        final float zLevel = 10.0F;

        tess.startDrawingQuads();

        tess.addVertexWithUV(screenPosition.getX() + 00.0D, screenPosition.getY() + 16.0D, zLevel, minU, maxV);
        tess.addVertexWithUV(screenPosition.getX() + 16.0D, screenPosition.getY() + 16.0D, zLevel, maxU, maxV);
        tess.addVertexWithUV(screenPosition.getX() + 16.0D, screenPosition.getY() + 00.0D, zLevel, maxU, minV);
        tess.addVertexWithUV(screenPosition.getX() + 00.0D, screenPosition.getY() + 00.0D, zLevel, minU, minV);
        tess.draw();
    }
}
