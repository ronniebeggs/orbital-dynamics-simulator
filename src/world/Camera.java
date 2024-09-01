package world;
import Jama.Matrix;
import util.Coordinate;
import util.Mesh;

public class Camera extends Entity {
    public Satellite target; // target satellite
    public double relativeDirection; // viewing angle relative to target at 0 (radians)
    public double distanceToTarget; // absolute distance between camera and the target (km)
    public Camera(Satellite initialTarget, double initialDistance, double initialTrueAnamoly) {
        super(0, 0, 0, 0, 0, 0);
        this.target = initialTarget;
        this.distanceToTarget = initialDistance;
        this.relativeDirection = initialTrueAnamoly;
    }
    public void setTarget(Satellite newTarget) {
        this.target = newTarget;
    }
    public Satellite getTarget() {
        return this.target;
    }
    public double getRelativeDirection() {
        return this.relativeDirection;
    }
    public double getDistanceToTarget() {
        return this.distanceToTarget;
    }
    public void rotateAroundTarget(double yawRotationDegrees) {
        this.relativeDirection += Math.toRadians(yawRotationDegrees);
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
        this.pitch = Math.toDegrees(Math.atan2(deltaZ, xyDistance));
    }
    /**
     * Move the camera closer toward the target by a specified distance.
     * @param distance absolute change in distance to move the camera by.
     * */
    public void moveTowardTarget(double distance) {
        this.distanceToTarget += distance;
    }

    public double distanceToViewPlane(Coordinate coordinate) {
        Coordinate cameraPosition = getPosition();
        double X = coordinate.getX() - cameraPosition.getX();
        double Y = coordinate.getY() - cameraPosition.getY();
        double Z = coordinate.getZ() - cameraPosition.getZ();
        // Theta = (thetaX, thetaY, thetaZ) -> tait-bryan angles
        Coordinate cameraDirection = getDirection();
        double pitch = Math.toRadians(cameraDirection.getX());
        double yaw = Math.toRadians(cameraDirection.getY());
        // I have no idea if this is going to work
        Matrix inversePitchRotation = new Matrix(new double[][]{
                new double[]{Math.cos(pitch), 0, -Math.sin(pitch)},
                new double[]{0, 1, 0},
                new double[]{Math.sin(pitch), 0, Math.cos(pitch)}
        });
        Matrix inverseYawRotation = new Matrix(new double[][]{
                new double[]{Math.cos(yaw), Math.sin(yaw), 0},
                new double[]{-Math.sin(yaw), Math.cos(yaw), 0},
                new double[]{0, 0, 1}
        });
        Matrix XYZMatrix = new Matrix(new double[][]{new double[]{X}, new double[]{Y}, new double[]{Z}});
        Matrix result = inversePitchRotation.times(inverseYawRotation.times(XYZMatrix));
        // get the distance to the rotated camera's view plane
        return result.get(0, 0);
    }

}
