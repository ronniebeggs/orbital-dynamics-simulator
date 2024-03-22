package world;
import util.Coordinate;

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


    /** @return `Coordinate` object containing the camera's pitch, yaw, roll angles. */
    public Coordinate getCameraTilt() {
        return new Coordinate(pitch, yaw, roll);
    }
    /**
     * Point the camera toward the target entity by shifting pitch and yaw directions.
     * @param target entity to point at.
     * */
    public void pointToward(Entity target) {
        Coordinate cameraPosition = getPosition();
        Coordinate targetPosition = target.getPosition();
        // calculate the yaw (right-left view)
        double deltaX = targetPosition.getX() - cameraPosition.getX();
        double deltaY = targetPosition.getY() - cameraPosition.getY();
        this.yaw = Math.toDegrees(Math.atan2(deltaY, deltaX));
        // calculate the pitch (up-down view)
        double deltaZ = targetPosition.getZ() - cameraPosition.getZ();
        double xyDistance = cameraPosition.distanceTo(targetPosition);
        this.pitch = Math.toDegrees(Math.atan2(deltaY, xyDistance));
    }
    /**
     * Move the camera closer toward the target by a specified distance.
     * @param target entity to move toward and remain pointed at.
     * @param distance absolute change in distance to move the camera by.
     * */
    public void moveTowardTarget(Entity target, double distance) {
        Coordinate cameraPosition = getPosition();
        Coordinate targetPosition = target.getPosition();
        // calculate the angle of the camera relative to the target in the x-y plane
        double deltaX = cameraPosition.getX() - targetPosition.getX();
        double deltaY = cameraPosition.getY() - targetPosition.getY();
        double relativeAngleXY = Math.atan2(deltaY, deltaX);
        double currentDistanceXY = cameraPosition.distanceTo(targetPosition);
        // calculate the angle of the camera relative to the target in the xy-z plane
        double deltaZ = cameraPosition.getZ() - targetPosition.getZ();
        double relativeAngleZ = Math.atan2(deltaZ, currentDistanceXY);
        // calculate the position changes that occur from this zooming movement
        double distance3D = cameraPosition.distance3D(targetPosition);
        double newZHeight = (distance3D - distance) * Math.sin(relativeAngleZ);
        double newXYDistance = (distance3D - distance) * Math.cos(relativeAngleZ);
        // update camera position and view angle
        this.setPosition(
                targetPosition.getX() + newXYDistance * Math.cos(relativeAngleXY),
                targetPosition.getY() + newXYDistance * Math.sin(relativeAngleXY),
                targetPosition.getZ() + newZHeight
        );
        pointToward(target);
    }


}
