package world;

import edu.princeton.cs.algs4.StdDraw;
import util.Coordinate;
import util.Mesh;

import java.awt.*;
import java.util.ArrayList;

public class Sphere extends Entity {
    public double radius;
    public int numSlices; // number of vertical slices around the sphere (n)
    public int numStacks; // number of horizontal slices around the sphere (m)
    public Sphere(double x, double y, double z, double pitch, double yaw, double roll, double r, int n, int m) {
        super(x, y, z, pitch, yaw, roll);
        this.radius = r;
        this.numSlices = n;
        this.numStacks = m;
        this.meshes = new ArrayList<>();
        createMesh();
    }
    /** Create a UV Sphere surface mesh according to the inputted number of slices/stacks. */
    public void createMesh() {
        double sliceAngle = (double) Math.TAU / numSlices;
        double stackAngle = (double) Math.PI / numStacks;

        Coordinate top = new Coordinate(xPosition, yPosition + radius, zPosition);
        Coordinate bottom = new Coordinate(xPosition, yPosition - radius, zPosition);

        // iterate through each slice (vertical section) and create meshes from the top down
        for (int n = 0; n < numSlices; n++) {
            // alternate slice color to better visualize mesh
            Color meshColor = StdDraw.BOOK_BLUE;
            if (n % 2 == 0) {
                meshColor = StdDraw.BOOK_RED;
            }

            double theta = n * sliceAngle; // angle parallel to the equator (longitude)
            double phi = stackAngle; // angle parallel to y-axis (latitude)

            // create top triangle
            double distanceFromYAxis = radius * Math.sin(phi);
            double distanceFromXZPlane = radius * Math.cos(phi);
            double y = distanceFromXZPlane;

            double x0 = distanceFromYAxis * Math.cos(theta);
            double z0 = distanceFromYAxis * Math.sin(theta);
            Coordinate v0 = Coordinate.fullPositionRotation(this, new Coordinate(x0, y, z0));

            double x1 = distanceFromYAxis * Math.cos(theta + sliceAngle);
            double z1 = distanceFromYAxis * Math.sin(theta + sliceAngle);
            Coordinate v1 = Coordinate.fullPositionRotation(this,new Coordinate(x1, y, z1));

            meshes.add(new Mesh(new Coordinate[]{top, v0, v1}, meshColor));

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
                v0 = Coordinate.fullPositionRotation(this, new Coordinate(x0, y, z0));

                x1 = xPosition + distanceFromYAxis * Math.cos(theta + sliceAngle);
                z1 = zPosition + distanceFromYAxis * Math.sin(theta + sliceAngle);
                v1 = Coordinate.fullPositionRotation(this, new Coordinate(x1, y, z1));

                meshes.add(new Mesh(new Coordinate[]{previousV0, v0, v1, previousV1}, meshColor));

                previousV0 = v0;
                previousV1 = v1;
            }

            // add bottom triangle
            meshes.add(new Mesh(new Coordinate[]{previousV0, bottom, previousV1}, meshColor));
        }
    }

}
