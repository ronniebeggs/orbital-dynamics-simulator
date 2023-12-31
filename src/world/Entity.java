package world;

import util.Coordinate;
public class Entity {
    public Coordinate position;
    public void setPosition(double xPosition, double yPosition) {
        this.position = new Coordinate(xPosition, yPosition);
    }
    public Coordinate getPosition() {
        return position;
    }
}
