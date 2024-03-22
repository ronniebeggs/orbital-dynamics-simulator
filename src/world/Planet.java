package world;

import java.awt.Color;

import edu.princeton.cs.algs4.StdDraw;
import util.Coordinate;
import util.Mesh;
import util.Transformations;

public class Planet extends Satellite {
    public String name; // planet name label
    public double radius; // radius of the planet (km)
    public int numSlices; // number of vertical slices around the sphere (n)
    public int numStacks; // number of horizontal slices around the sphere (m)
    public Planet(String name, Color color, double radius, double mass) {
        this(name, null, color, radius, mass, 0, 0, 0);
    }
    public Planet(String name, Planet parent, Color color, double radius, double mass, double orbitalRadius, double orbitalVelocity, double trueAnomaly) {
        super(parent, color, mass, orbitalRadius, orbitalVelocity, trueAnomaly);
        this.name = name;
        this.radius = radius;
        this.numSlices = 24;
        this.numStacks = 12;
        createMesh();
    }

    /** Create a UV Sphere surface mesh according to the inputted number of slices/stacks. */
    public void createMesh() {
        Coordinate planetPosition = getPosition();
        double xPosition = planetPosition.getX();
        double yPosition = planetPosition.getY();
        double zPosition = planetPosition.getZ();

        double sliceAngle = (double) Math.TAU / numSlices;
        double stackAngle = (double) Math.PI / numStacks;

        Coordinate top = new Coordinate(xPosition, yPosition + radius, zPosition);
        Coordinate bottom = new Coordinate(xPosition, yPosition - radius, zPosition);

        Color meshColor = StdDraw.BOOK_BLUE;
        // iterate through each slice (vertical section) and create meshes from the top down
        for (int n = 0; n < numSlices; n++) {
            double theta = n * sliceAngle; // angle parallel to the equator (longitude)
            double phi = stackAngle; // angle parallel to y-axis (latitude)

            // create top triangle
            double distanceFromYAxis = radius * Math.sin(phi);
            double distanceFromXZPlane = radius * Math.cos(phi);
            double y = distanceFromXZPlane;

            double x0 = distanceFromYAxis * Math.cos(theta);
            double z0 = distanceFromYAxis * Math.sin(theta);
            Coordinate v0 = Transformations.rotateYaw(this, new Coordinate(x0, y, z0));

            double x1 = distanceFromYAxis * Math.cos(theta + sliceAngle);
            double z1 = distanceFromYAxis * Math.sin(theta + sliceAngle);
            Coordinate v1 = Transformations.rotateYaw(this,new Coordinate(x1, y, z1));

            meshes.add(new Mesh(this, new Coordinate[]{top, v0, v1}, meshColor));

            // save previously computed coordinates to limit duplicates
            Coordinate previousV0 = v0;
            Coordinate previousV1 = v1;

            // render middle quad meshes
            for (int m = 1; m < numStacks - 1; m++) {
                phi += stackAngle;

                distanceFromYAxis = radius * Math.sin(phi);
                distanceFromXZPlane = radius * Math.cos(phi);
                y = distanceFromXZPlane;

                x0 = distanceFromYAxis * Math.cos(theta);
                z0 = distanceFromYAxis * Math.sin(theta);
                v0 = Transformations.rotateYaw(this, new Coordinate(x0, y, z0));

                x1 = xPosition + distanceFromYAxis * Math.cos(theta + sliceAngle);
                z1 = zPosition + distanceFromYAxis * Math.sin(theta + sliceAngle);
                v1 = Transformations.rotateYaw(this, new Coordinate(x1, y, z1));

                meshes.add(new Mesh(this, new Coordinate[]{previousV0, v0, v1, previousV1}, meshColor));

                previousV0 = v0;
                previousV1 = v1;
            }

            // add bottom triangle
            meshes.add(new Mesh(this, new Coordinate[]{previousV0, bottom, previousV1}, meshColor));
        }
    }


}
