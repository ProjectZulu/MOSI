package mosi.utilities;

/**
 * Essentially an immutable version of java.awt.Point
 */
public class Coord {

    public final int x;
    public final int z;

    public Coord(int x, int z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Coord)) {
            return false;
        }
        Coord grid = (Coord) obj;
        return x == grid.x && z == grid.z;
    }

    @Override
    public int hashCode() {
        return x ^ RotateLeft(z, 16);
    }

    private int RotateLeft(int value, int count) {
        return (value << count) | (value >> (32 - count));
    }

    public float distance(Coord coord) {
        return (float) Math.sqrt(distanceSQ(coord));
    }

    public float distanceSQ(Coord coord) {
        float xDis = x - coord.x;
        float zDis = z - coord.z;
        return xDis * xDis + zDis * zDis;
    }
}
