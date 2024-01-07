package world;

public class Camera extends Entity {
    public Satellite target; // target satellite
    public double direction; // viewing angle within xy-plane
    public double distance;
    public Camera(Satellite initialTarget, double initialDistance) {
        this.target = initialTarget;
        this.distance = initialDistance;
    }
    public double getDistance() {
        return this.distance;
    }
    public void setDirection(double initialDirection) {
        this.direction = initialDirection;
    }
    public void shiftDirection(double directionChange) {
        this.direction += directionChange;
    }
    public double getDirection() {
        return this.direction;
    }
}
