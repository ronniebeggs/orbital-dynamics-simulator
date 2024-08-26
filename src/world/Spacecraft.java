package world;

import util.Coordinate;
import util.Mesh;
import util.Transformations;

import java.awt.*;

public class Spacecraft extends Satellite {
    public double shipSize; // size to be displayed on the screen
    public Spacecraft(Satellite parent, Color color, double mass, double orbitalRadius, double orbitalVelocity, double trueAnomaly) {
        super(parent, color, mass, orbitalRadius, orbitalVelocity, trueAnomaly);
        this.shipSize = 1000;
        createMesh();
    }
    /**
     * Engage the Spacecraft's thrust in a specific direction.
     * @param thrustDirection direction of thrust (1: forward, -1: backward)
     * @param percentIncrease percent change in the velocity in the specified direction.
     * */
    public void engageThrust(int thrustDirection, double percentIncrease) {
        Coordinate velocity = getVelocity();
        double straightVelocity = Math.sqrt(
                Math.pow(velocity.getX(), 2) + Math.pow(velocity.getY(), 2)
        );
        double thrustMagnitude = thrustDirection * straightVelocity * percentIncrease;
        double craftDirection = Math.atan2(velocity.getY(), velocity.getX());
        // apply thrust by incrementing the spacecraft's velocity by a calculated amount
        velocity.shiftX(thrustMagnitude * Math.cos(craftDirection));
        velocity.shiftY(thrustMagnitude * Math.sin(craftDirection));
    }
    /** @return distance between the spacecraft and the first lead position in the deque. */
    public double distanceToFirstLead() {
        Coordinate nextLeadPosition = getLeadPositions().getFirst();
        return nextLeadPosition.distanceTo(getPosition());
    }

    public double radius = 1000; // radius of the planet (km)
    public int numSlices = 24; // number of vertical slices around the sphere (n)
    public int numStacks = 12; // number of horizontal slices around the sphere (m)

    /** Create a UV Sphere surface mesh according to the inputted number of slices/stacks. */
    public void createMesh() {
        Coordinate planetPosition = getPosition();
        double xPosition = planetPosition.getX();
        double yPosition = planetPosition.getY();
        double zPosition = planetPosition.getZ();

        double sliceAngle = (double) Math.TAU / numSlices;
        double stackAngle = (double) Math.PI / numStacks;

        Coordinate top = new Coordinate(xPosition, yPosition, zPosition + radius);
        Coordinate bottom = new Coordinate(xPosition, yPosition, zPosition - radius);

        Color tempColor;
        Color meshColor = color;
        // iterate through each slice (vertical section) and create meshes from the top down
        for (int n = 0; n < numSlices; n++) {
            double theta = n * sliceAngle; // angle parallel to the equator (longitude)
            double phi = stackAngle; // angle parallel to y-axis (latitude)

            // create top triangle
            double distanceFromZAxis = radius * Math.sin(phi);
            double distanceFromXYPlane = radius * Math.cos(phi);
            double z = distanceFromXYPlane;

            double x0 = distanceFromZAxis * Math.cos(theta);
            double y0 = distanceFromZAxis * Math.sin(theta);
            Coordinate v0 = Transformations.rotateYaw(this, new Coordinate(x0, y0, z));

            double x1 = distanceFromZAxis * Math.cos(theta + sliceAngle);
            double y1 = distanceFromZAxis * Math.sin(theta + sliceAngle);
            Coordinate v1 = Transformations.rotateYaw(this,new Coordinate(x1, y1, z));

            meshes.add(new Mesh(this, new Coordinate[]{top, v0, v1}, meshColor));

            // save previously computed coordinates to limit duplicates
            Coordinate previousV0 = v0;
            Coordinate previousV1 = v1;

            // render middle quad meshes
            for (int m = 1; m < numStacks - 1; m++) {
                phi += stackAngle;

                distanceFromZAxis = radius * Math.sin(phi);
                distanceFromXYPlane = radius * Math.cos(phi);
                z = distanceFromXYPlane;

                x0 = distanceFromZAxis * Math.cos(theta);
                y0 = distanceFromZAxis * Math.sin(theta);
                v0 = Transformations.rotateYaw(this, new Coordinate(x0, y0, z));

                x1 = xPosition + distanceFromZAxis * Math.cos(theta + sliceAngle);
                y1 = yPosition + distanceFromZAxis * Math.sin(theta + sliceAngle);
                v1 = Transformations.rotateYaw(this, new Coordinate(x1, y1, z));

                meshes.add(new Mesh(this, new Coordinate[]{previousV0, v0, v1, previousV1}, meshColor));

                previousV0 = v0;
                previousV1 = v1;
            }

            // add bottom triangle
            meshes.add(new Mesh(this, new Coordinate[]{previousV0, bottom, previousV1}, meshColor));
        }
    }
}
