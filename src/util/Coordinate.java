package util;

public class Coordinate {
    public double x;
    public double y;
    public Coordinate(double xInitial, double yInitial) {
        this.x = xInitial;
        this.y = yInitial;
    }
    /** @param x clear and set the coordinate's x-component value. */
    public void setX(double x) {
        this.x = x;
    }
    /** @param y clear and set the coordinate's y-component value. */
    public void setY(double y) {
        this.y = y;
    }
    /** @param xChange value to add to the coordinate's x-component. */
    public void shiftX(double xChange) {
        this.x += xChange;
    }
    /** @param yChange value to add to the coordinate's y-component. */
    public void shiftY(double yChange) {
        this.y += yChange;
    }
    /** @return the coordinate's x-component. */
    public double getX() {
        return this.x;
    };
    /** @return the coordinate's y-component. */
    public double getY() {
        return this.y;
    };
    /**
     * Return the Euclidean distance to the target `Coordinate`.
     * @param other target `Coordinate`.
     * @return euclidean distance between coordinates.
     * */
    public double distanceTo(Coordinate other) {
        double deltaXSquared = Math.pow(getX() - other.getX(), 2);
        double deltaYSquared = Math.pow(getY() - other.getY(), 2);
        return Math.sqrt(deltaXSquared + deltaYSquared);
    }
    /**
     * Return the angle between coordinates, relative to the target.
     * @param other target `Coordinate`.
     * @return angle between coordinates.
     * */
    public double angleBetween(Coordinate other) {
        double deltaX = other.getX() - getX();
        double deltaY = other.getY() - getY();
        return Math.atan2(deltaY, deltaX);
    }
    /** @return new `Coordinate` object with the corresponding component values. */
    public Coordinate copyCoordinate() {
        return new Coordinate(getX(), getY());
    }
    /** @return component values stored in an array. */
    public double[] toArray() {
        return new double[]{x, y};
    }
}
