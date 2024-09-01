package world;

import util.Coordinate;

/**
 * An `Entity` is an object placed at some position within the world.
 * It's the parent class for all `Satellite`s and the `Camera`.
 * */
public class Entity {
    public Coordinate position;
    public double pitch; // vertical rotation (looking up and down)
    public double yaw; // rotation within xz-plane (turning right and left)
    public double roll; // rotation relative to viewing plane (barrel roles)
    public Entity(double x, double y, double z, double pitch, double yaw, double roll) {
        this.position = new Coordinate(x, y, z);
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }
    public void setPosition(double x, double y, double z) {
        this.position.setX(x);
        this.position.setY(y);
        this.position.setZ(z);
    }
    public void setPosition(double x, double y) {
        setPosition(x, y, 0);
    }
    public Coordinate getPosition() {
        return position;
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
