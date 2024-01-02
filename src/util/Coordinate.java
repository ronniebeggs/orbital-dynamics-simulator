package util;

public class Coordinate {
    public double x;
    public double y;

    public Coordinate(double xInitial, double yInitial) {
        this.x = xInitial;
        this.y = yInitial;
    }
    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    public void shiftX(double deltaX) {
        this.x += deltaX;
    }
    public void shiftY(double deltaY) {
        this.y += deltaY;
    }
    public double getX() { return this.x; };
    public double getY() {
        return this.y;
    };
    public double distanceTo(Coordinate other) {
        double deltaXSquared = Math.pow(getX() - other.getX(), 2);
        double deltaYSquared = Math.pow(getY() - other.getY(), 2);
        return Math.sqrt(deltaXSquared + deltaYSquared);
    }
    public double[] toArray() {
        return new double[]{x, y};
    }
}
