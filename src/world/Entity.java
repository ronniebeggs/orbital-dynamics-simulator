package world;

import util.Coordinate;

/**
 * An `Entity` is an object placed at some position within the world.
 * It's the parent class for all `Satellite`s and the `Camera`.
 * */
public class Entity {
    public Coordinate position;
    /**
     * Set the position of the entity by instantiating a new `Coordinate`.
     * @param xPosition entity's position along the x-axis.
     * @param yPosition entity's position along the y-axis.
     * */
    public void setPosition(double xPosition, double yPosition) {
        this.position = new Coordinate(xPosition, yPosition);
    }
    /**
     * @return the entity's current position `Coordinate`.
     * */
    public Coordinate getPosition() {
        return position;
    }
}
