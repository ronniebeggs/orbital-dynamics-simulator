package util;

import world.Entity;

public class Transformations {
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
//        double dX = x * Math.cos(yawRadians) + z * Math.sin(yawRadians);
//        double dY = y;
//        double dZ = -x * Math.sin(yawRadians) + z * Math.cos(yawRadians);
        double dX = x * Math.cos(yawRadians) + y * Math.sin(yawRadians);
        double dY = -x * Math.sin(yawRadians) + y * Math.cos(yawRadians);
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
}
