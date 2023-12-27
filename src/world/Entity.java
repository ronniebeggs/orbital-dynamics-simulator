package world;

import util.Coordinate;
public class Entity {
    public double xPosition;
    public double yPosition;
    public Entity(double x, double y) {
        xPosition = x;
        yPosition = y;
    }
    public Coordinate getPosition() {
        return new Coordinate(xPosition, yPosition);
    }
}
