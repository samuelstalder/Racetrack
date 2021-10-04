package ch.zhaw.pm2.racetrack;

/**
 * Holds a position (vector to x,y-position of the car on the track grid)
 * or a velocity vector (x,y-components of the velocity vector of a car).
 *
 * Created by mach 21.01.2020
 */
public final class PositionVector implements Cloneable {
    private int x; // horizontal component (position / velocity)
    private int y; // vertical component (position / velocity)

    /**
     * Enum representing a direction on the track grid.
     * Also representing the possible acceleration values.
     */
    public enum Direction {
        DOWN_LEFT(new PositionVector(-1, 1)),
        DOWN(new PositionVector(0, 1)),
        DOWN_RIGHT(new PositionVector(1, 1)),
        LEFT(new PositionVector(-1, 0)),
        NONE(new PositionVector(0, 0)),
        RIGHT(new PositionVector(1, 0)),
        UP_LEFT(new PositionVector(-1, -1)),
        UP(new PositionVector(0, -1)),
        UP_RIGHT(new PositionVector(1, -1));

        public final PositionVector vector;
        Direction(final PositionVector v) {
            vector = v;
        }
    }

    public PositionVector(final int x, final int y) {
        this.y = y;
        this.x = x;
    }

    public PositionVector(final PositionVector other) {
        x = other.getX();
        y = other.getY();
    }

    public PositionVector() {
        x = 0;
        y = 0;
    }

    public int getX() {
        return x;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(final int y) {
        this.y = y;
    }


    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof PositionVector)) throw new ClassCastException();
        final PositionVector otherPositionVector = (PositionVector) other;
        return y == otherPositionVector.getY() && x == otherPositionVector.getX();
    }

    @Override
    public int hashCode() {
        return x ^ y;
    }

    @Override
    public String toString() {
        return  "(X:" + x + ", Y:" + y + ")";
    }
}
