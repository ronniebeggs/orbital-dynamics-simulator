package util;

public class Coordinate {
    public double x;
    public double y;
    public double z;
    public Coordinate(double xInitial, double yInitial, double zInitial) {
        this.x = xInitial;
        this.y = yInitial;
        this.z = zInitial;
    }
    public Coordinate(double xInitial, double yInitial) {
        this(xInitial, yInitial, 0);
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
    /** @return the coordinate's z-component. */
    public double getZ() {
        return this.z;
    };
    /**
     * Return the distance to the target `Coordinate`.
     * @param other target `Coordinate`.
     * @return euclidean distance between coordinates.
     * */
    public double distance3D(Coordinate other) {
        double deltaXSquared = Math.pow(getX() - other.getX(), 2);
        double deltaYSquared = Math.pow(getY() - other.getY(), 2);
        double deltaZSquared = Math.pow(getZ() - other.getZ(), 2);
        return Math.sqrt(deltaXSquared + deltaYSquared + deltaZSquared);
    }
    public double magnitude() {
        return Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2) + Math.pow(getZ(), 2));
    }
    /**
     * Return the distance to the target `Coordinate` within the XY-plane.
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
}
