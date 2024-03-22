package world;

public class Camera extends Entity {
    public Satellite target; // target satellite
    public double absoluteDirection; // viewing angle relative to xy-plane (radians)
    public double relativeDirection; // viewing angle relative to target at 0 (radians)
    public double distanceToTarget; // absolute distance between camera and the target (km)
    public Camera(Satellite initialTarget, double initialDistance) {
        super(0, 0, 0, 0, 0, 0);
        this.target = initialTarget;
        this.distanceToTarget = initialDistance;
    }
    public void setTarget(Satellite newTarget) {
        this.target = newTarget;
    }
    public Satellite getTarget() {
        return this.target;
    }
    public void setAbsoluteDirection(double newDirection) {
        this.absoluteDirection = newDirection;
    }
    public double getAbsoluteDirection() {
        return this.absoluteDirection;
    }
    public void setRelativeDirection(double newDirection) {
        this.relativeDirection = newDirection;
    }
    public double getRelativeDirection() {
        return this.relativeDirection;
    }
    public void setDistanceToTarget(double newDistance) {
        this.distanceToTarget = newDistance;
    }
    public double getDistanceToTarget() {
        return this.distanceToTarget;
    }
}
