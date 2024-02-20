package world;
import util.Coordinate;

/**
 * Camera object exists within the simulation. Other objects are rendered relative
 * to the camera and its position/direction attributes.
 * */
public class Camera extends Entity {
    public Camera(double x, double y, double z, double pitch, double yaw, double roll) {
        super(x, y, z, pitch, yaw, roll);
    }
    /**
     * Move the camera forward/backward relative to the camera's current yaw direction in the xz-plane.
     * @param distance simulation distance to move (positive: forward, negative: backward)
     * */
    public void moveFrontal(double distance) {
        moveInDirection(distance, this.yaw);
    }
    /**
     * Move the camera right/left relative to the camera's current yaw direction in the xz-plane.
     * @param distance simulation distance to move (positive: right, negative: left)
     * */
    public void moveLateral(double distance) {
        moveInDirection(distance, this.yaw - 90);
    }
    private void moveInDirection(double distance, double angle) {
        double deltaX = distance * Math.cos(Math.toRadians(angle));
        double deltaZ = distance * Math.sin(Math.toRadians(angle));
        this.xPosition += deltaX;
        this.zPosition += deltaZ;
    }
    /**
     * Move the camera up/down relative along the y-axis.
     * @param distanceChange simulation distance to move (positive: up, negative: down)
     * */
    public void moveTransverse(double distanceChange) {
        this.yPosition += distanceChange;
    }
    /**
     * Turn the camera up/down.
     * @param degreeChange change in the pitch angle (positive: up, negative: down)
     * */
    public void rotatePitch(double degreeChange) {
        this.pitch += degreeChange;
    }
    /**
     * Turn the camera right/left.
     * @param degreeChange change in the yaw angle (positive: right, negative: left)
     * */
    public void rotateYaw(double degreeChange) {
        // TODO: floor mod degree to avoid overflow (view angles could be integers with limited precisions)
        this.yaw += degreeChange;
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
        double deltaZ = targetPosition.getZ() - cameraPosition.getZ();
        this.yaw = Math.toDegrees(Math.atan2(deltaZ, deltaX));
        // calculate the pitch (up-down view)
        double deltaY = targetPosition.getY() - cameraPosition.getY();
        double xzDistance = cameraPosition.distanceXZ(targetPosition);
        this.pitch = Math.toDegrees(Math.atan2(deltaY, xzDistance));
    }
    /**
     * Horizontally orbit the camera around the target entity by a certain degree.
     * @param target entity to orbit around and remain pointed at.
     * @param degree degree of horizontal orbit to shift around the entity.
     * */
    public void rotateAroundHorizontal(Entity target, double degree) {
        Coordinate cameraPosition = getPosition();
        Coordinate targetPosition = target.getPosition();
        // calculate the angle of the camera relative to the target
        double deltaX1 = cameraPosition.getX() - targetPosition.getX();
        double deltaZ1 = cameraPosition.getZ() - targetPosition.getZ();
        double relativeToTarget = Math.atan2(deltaZ1, deltaX1);
        // calculate the new camera position relative to the target position
        double xzDistance = cameraPosition.distanceXZ(targetPosition);
        double deltaX2 = xzDistance * Math.cos(relativeToTarget + Math.toRadians(degree));
        double deltaZ2 = xzDistance * Math.sin(relativeToTarget + Math.toRadians(degree));
        // update camera position and view angle
        this.xPosition = targetPosition.getX() + deltaX2;
        this.zPosition = targetPosition.getZ() + deltaZ2;
        pointToward(target);
    }
    /**
     * Vertically orbit the camera around the target entity by a certain degree.
     * @param target entity to orbit around and remain pointed at.
     * @param degree degree of vertical orbit to shift around the entity.
     * */
    public void rotateAroundVertical(Entity target, double degree) {
        Coordinate cameraPosition = getPosition();
        Coordinate targetPosition = target.getPosition();
        // calculate the angle of the camera relative to the target in the x-z plane
        double deltaX1 = cameraPosition.getX() - targetPosition.getX();
        double deltaZ1 = cameraPosition.getZ() - targetPosition.getZ();
        double relativeAngleXZ = Math.atan2(deltaZ1, deltaX1);
        // calculate the angle of the camera relative to the target in the xz-y plane
        double deltaXZ1 = Math.sqrt(Math.pow(deltaX1, 2) + Math.pow(deltaZ1, 2));
        double deltaY1 = cameraPosition.getY() - targetPosition.getY();
        double relativeAngleY = Math.atan2(deltaY1, deltaXZ1);
        // compute angle of the camera's new position relative to the target
        double newTargetAngle = relativeAngleY + Math.toRadians(degree);
        // adjust angle if it exceeds a boundary to prevent fully rotating over the target
        if (newTargetAngle >= Math.PI / 2) {
            newTargetAngle = Math.PI / 2;
        } else if (newTargetAngle <= -Math.PI / 2) {
            newTargetAngle = -Math.PI / 2;
        }
        // calculate the new camera position relative to the target position
        double distance3D = cameraPosition.distance3D(targetPosition);
        double deltaXZ2 = distance3D * Math.cos(newTargetAngle);
        double deltaY2 = distance3D * Math.sin(newTargetAngle);
        // update camera position and view angle
        this.xPosition = targetPosition.getX() + deltaXZ2 * Math.cos(relativeAngleXZ);
        this.zPosition = targetPosition.getZ() + deltaXZ2 * Math.sin(relativeAngleXZ);
        this.yPosition = targetPosition.getY() + deltaY2;
        pointToward(target);
    }
    /**
     * Move the camera closer toward the target by a specified distance.
     * @param target entity to move toward and remain pointed at.
     * @param distance absolute change in distance to move the camera by.
     * */
    public void moveTowardTarget(Entity target, double distance) {
        Coordinate cameraPosition = getPosition();
        Coordinate targetPosition = target.getPosition();
        // calculate the angle of the camera relative to the target in the x-z plane
        double deltaX = cameraPosition.getX() - targetPosition.getX();
        double deltaZ = cameraPosition.getZ() - targetPosition.getZ();
        double relativeAngleXZ = Math.atan2(deltaZ, deltaX);
        double currentDistanceXZ = cameraPosition.distanceXZ(targetPosition);
        // calculate the angle of the camera relative to the target in the xz-y plane
        double deltaY = cameraPosition.getY() - targetPosition.getY();
        double relativeAngleY = Math.atan2(deltaY, currentDistanceXZ);
        // calculate the position changes that occur from this zooming movement
        double distance3D = cameraPosition.distance3D(targetPosition);
        double newYHeight = (distance3D - distance) * Math.sin(relativeAngleY);
        double newXZDistance = (distance3D - distance) * Math.cos(relativeAngleY);
        // update camera position and view angle
        this.xPosition = targetPosition.getX() + newXZDistance * Math.cos(relativeAngleXZ);
        this.zPosition = targetPosition.getZ() + newXZDistance * Math.sin(relativeAngleXZ);
        this.yPosition = targetPosition.getY() + newYHeight;
        pointToward(target);
    }
    /** @return `Coordinate` object containing the camera's pitch, yaw, roll angles. */
    public Coordinate getCameraTilt() {
        return new Coordinate(pitch, yaw, roll);
    }
    /**
     * Calculate the distance of a target entity orthogonal to the camera's viewing plane.
     * @param entity target to calculate relative distance to.
     * */
    public double distanceToViewPlane(Entity entity) {
        Coordinate cameraPosition = getPosition();
        Coordinate entityPosition = entity.getPosition();
        // project the entity onto the xz-plane defined by the camera's pitch angle
        double pitchRadians = Math.toRadians(getCameraTilt().getX());
        double deltaY = entityPosition.getY() - cameraPosition.getY();
        double distance3D = entityPosition.distance3D(cameraPosition);
        double angleToXZ = Math.asin(deltaY / distance3D);
        double distanceRelativeToCameraXZ = distance3D * Math.cos(angleToXZ - pitchRadians);
        // project the entity onto x-axis defined by the camera's yaw angle
        double yawRadians = Math.toRadians(getCameraTilt().getY());
        double deltaX = entityPosition.getX() - cameraPosition.getX();
        double deltaZ = entityPosition.getZ() - cameraPosition.getZ();
        double angleRelativeToX = Math.atan2(deltaZ, deltaX);
        // determine the distance of the entity along this relative axis
        return distanceRelativeToCameraXZ * Math.cos(angleRelativeToX - yawRadians);
    }
}
