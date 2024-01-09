package util;

import world.Entity;

public class Coordinate {
    public double x;
    public double y;
    public double z;

    public Coordinate(double xInitial, double yInitial, double zInitial) {
        this.x = xInitial;
        this.y = yInitial;
        this.z = zInitial;
    }
    public double getX() { return this.x; };
    public double getY() {
        return this.y;
    };
    public double getZ() {
        return this.z;
    };
    public double distance3D(Coordinate other) {
        double deltaXSquared = Math.pow(getX() - other.getX(), 2);
        double deltaYSquared = Math.pow(getY() - other.getY(), 2);
        double deltaZSquared = Math.pow(getZ() - other.getZ(), 2);
        return Math.sqrt(deltaXSquared + deltaYSquared + deltaZSquared);
    }
    public double distanceXZ(Coordinate other) {
        double deltaXSquared = Math.pow(getX() - other.getX(), 2);
        double deltaZSquared = Math.pow(getZ() - other.getZ(), 2);
        return Math.sqrt(deltaXSquared + deltaZSquared);
    }
    /**
     * Rotate relative position in the yaw direction.
     * @param entity target to transform position relative to.
     * @param relativePosition position relative to the target object.
     * @return position transformed to real simulation axes.
     * */
    public static Coordinate rotateYaw(Entity entity, Coordinate relativePosition) {
        // calculate positions relative to the object's center
        double x = relativePosition.getX();
        double y = relativePosition.getY();
        double z = relativePosition.getZ();

        // yaw transformation
        double yawRadians = -Math.toRadians(entity.getDirection().getY());
        double dX = x * Math.cos(yawRadians) + z * Math.sin(yawRadians);
        double dY = y;
        double dZ = -x * Math.sin(yawRadians) + z * Math.cos(yawRadians);

        // transform relative positions to real simulation positions
        Coordinate entityPosition = entity.getPosition();
        return new Coordinate(
                dX + entityPosition.getX(),
                dY + entityPosition.getY(),
                dZ + entityPosition.getZ()
        );
    }
    /**
     * Rotate relative position in the pitch direction.
     * @param entity target to transform position relative to.
     * @param relativePosition position relative to the target object.
     * @return position transformed to real simulation axes.
     * */
    public static Coordinate rotatePitch(Entity entity, Coordinate relativePosition) {
        // calculate positions relative to the object's center
        double x = relativePosition.getX();
        double y = relativePosition.getY();
        double z = relativePosition.getZ();

        // pitch transformation
        double pitchRadians = Math.toRadians(entity.getDirection().getX());
        double dX = x * Math.cos(pitchRadians) - y * Math.sin(pitchRadians);
        double dY = x * Math.sin(pitchRadians) + y * Math.cos(pitchRadians);
        double dZ = z;

        // transform relative positions to real simulation positions
        Coordinate entityPosition = entity.getPosition();
        return new Coordinate(
                dX + entityPosition.getX(),
                dY + entityPosition.getY(),
                dZ + entityPosition.getZ()
        );
    }
    /**
     * Rotate relative position in the pitch direction.
     * @param entity target to transform position relative to.
     * @param relativePosition position relative to the target object.
     * @return position transformed to real simulation axes.
     * */
    public static Coordinate rotateRoll(Entity entity, Coordinate relativePosition) {
        // calculate positions relative to the object's center
        double x = relativePosition.getX();
        double y = relativePosition.getY();
        double z = relativePosition.getZ();

        // roll transformation
        double rollRadians = Math.toRadians(entity.getDirection().getZ());
        double dX = x;
        double dY = -z * Math.sin(rollRadians) + y * Math.cos(rollRadians);
        double dZ = z * Math.cos(rollRadians) + y * Math.sin(rollRadians);

        // transform relative positions to real simulation positions
        Coordinate entityPosition = entity.getPosition();
        return new Coordinate(
                dX + entityPosition.getX(),
                dY + entityPosition.getY(),
                dZ + entityPosition.getZ()
        );
    }
    /**
     * Transform positions relative to an object to real simulation positions.
     * @param entity target to transform position relative to.
     * @param relativePosition position relative to the target object.
     * @return position transformed to real simulation axes.
     * */
    public static Coordinate fullPositionRotation(Entity entity, Coordinate relativePosition) {
        return rotateYaw(entity, relativePosition);
//        // calculate positions relative to the object's center
//        double x = relativePosition.getX();
//        double z = relativePosition.getZ();
//        double y = relativePosition.getY();
//
//        Coordinate entityDirection = entity.getDirection();
//        double pitchRadians = Math.toRadians(entityDirection.getX()); // pitch
//        double yawRadians = Math.toRadians(entityDirection.getY()); // yaw
//        double rollRadians = Math.toRadians(entityDirection.getZ()); // roll
//
//        double dX = Math.cos(yawRadians) * (Math.sin(rollRadians) * z + Math.cos(rollRadians) * x) - Math.sin(yawRadians) * y;
//        double dY = Math.sin(pitchRadians) * (Math.cos(yawRadians) * y + Math.sin(yawRadians) * (Math.sin(rollRadians) * z + Math.cos(rollRadians) * x)) + Math.cos(pitchRadians) * (Math.cos(rollRadians) * z - Math.sin(rollRadians) * x);
//        double dZ = Math.cos(pitchRadians) * (Math.cos(yawRadians) * y + Math.sin(yawRadians) * (Math.sin(rollRadians) * z + Math.cos(rollRadians) * x)) - Math.sin(pitchRadians) * (Math.cos(rollRadians) * z - Math.sin(rollRadians) * x);
//
//        // transform relative positions to real simulation positions
//        Coordinate entityPosition = entity.getPosition();
//        return new Coordinate(
//                dX + entityPosition.getX(),
//                dY + entityPosition.getY(),
//                dZ + entityPosition.getZ()
//        );
    }
    public double[] toArray() {
        return new double[]{x, y, z};
    }
}
