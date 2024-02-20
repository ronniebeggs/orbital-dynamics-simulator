package world;

import util.Coordinate;

/**
 * An `Entity` is an object placed at some position within the world.
 * It's the parent class for all `Satellite`s and the `Camera`.
 * */
public class Entity {
    public double xPosition;
    public double yPosition;
    public double zPosition;
    public double pitch; // vertical rotation (looking up and down)
    public double yaw; // rotation within xz-plane (turning right and left)
    public double roll; // rotation relative to viewing plane (barrel roles)
    public Entity(double x, double y, double z, double pitch, double yaw, double roll) {
        this.xPosition = x;
        this.yPosition = y;
        this.zPosition = z;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }
    public Coordinate getPosition() {
        return new Coordinate(xPosition, yPosition, zPosition);
    }
    public void setDirection(double pitch, double yaw, double roll) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }
    public Coordinate getDirection() {
        return new Coordinate(pitch, yaw, roll);
    }
}
