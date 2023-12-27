package util;

public class Coordinate {
    public double x;
    public double y;

    public Coordinate(double xInitial, double yInitial) {
        this.x = xInitial;
        this.y = yInitial;
    }
    public double getX() { return this.x; };
    public double getY() {
        return this.y;
    };
    public double distance(Coordinate other) {
        double deltaXSquared = Math.pow(getX() - other.getX(), 2);
        double deltaYSquared = Math.pow(getY() - other.getY(), 2);
        return Math.sqrt(deltaXSquared + deltaYSquared);
    }
    public double[] toArray() {
        return new double[]{x, y};
    }
}
