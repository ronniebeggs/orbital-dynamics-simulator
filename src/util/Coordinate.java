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
    public double magnitude() {
        return Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2) + Math.pow(getZ(), 2));
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
    /**
     * Computes the cross product of vectors v1 and v2.
     * @param v1 represents the pointer finger vector.
     * @param v2 represents the middle finger vector.
     * @return cross product vector.
     * */
    public static Coordinate crossProduct(Coordinate v1, Coordinate v2) {
        double x = v1.getY() * v2.getZ() - v1.getZ() * v2.getY();
        double y = v1.getZ() * v2.getX() - v1.getX() * v2.getZ();
        double z = v1.getX() * v2.getY() - v1.getY() * v2.getX();
        return new Coordinate(x, y, z);
    }
    /**
     * Computes the dot product of vectors v1 and v2.
     * @return result dot product.
     * */
    public static double dotProduct(Coordinate v1, Coordinate v2) {
        return (v1.getX() * v2.getX()) + (v1.getY() * v2.getY()) + (v1.getZ() * v2.getZ());
    }
    /**
     * Normalize a given vector input (make magnitude == 1)
     * @param vector input vector.
     * @return resulting normalized vector.
     * */
    public static Coordinate normalize(Coordinate vector) {
        double magnitude = vector.magnitude();
        return new Coordinate(
                vector.getX() / magnitude,
                vector.getY() / magnitude,
                vector.getZ() / magnitude
        );
    }
    public double[] toArray() {
        return new double[]{x, y, z};
    }
}
