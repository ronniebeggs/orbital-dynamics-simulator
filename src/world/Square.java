package world;

import edu.princeton.cs.algs4.StdDraw;
import util.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class Square extends Entity {
    public int sideLength;
    public List<Coordinate> vertices;

    public Square(int x, int y, int z, int length) {
        super(x, y, z);
        sideLength = length;
        createVertices();
    }

    public void createVertices() {
        vertices = new ArrayList<>();
        int halfLength = sideLength / 2;
        // insert vertices clockwise from bottom left corner
        Coordinate vertex1 = new Coordinate(xPosition - halfLength, yPosition - halfLength, zPosition);
        Coordinate vertex2 = new Coordinate(xPosition - halfLength, yPosition + halfLength, zPosition);
        Coordinate vertex3 = new Coordinate(xPosition + halfLength, yPosition + halfLength, zPosition);
        Coordinate vertex4 = new Coordinate(xPosition + halfLength, yPosition - halfLength, zPosition);
        vertices.add(vertex1);
        vertices.add(vertex2);
        vertices.add(vertex3);
        vertices.add(vertex4);
    }


    public void render() {
        StdDraw.setPenColor(StdDraw.RED);
        Coordinate origin = new Coordinate(0, 0, 0);
        double[] xVertices = new double[4];
        double[] yVertices = new double[4];
        for (int i = 0; i < vertices.size(); i++) {
            Coordinate transformed = transformCoordinate(vertices.get(i), origin);
            xVertices[i] = transformed.getX() + 300;
            yVertices[i] = transformed.getY() + 300;
        }
        StdDraw.filledPolygon(xVertices, yVertices);
    }

    public Coordinate transformCoordinate(Coordinate entityCoord, Coordinate cameraCoord) {
        double dX = entityCoord.getX() - cameraCoord.getX();
        double dY = entityCoord.getY() - cameraCoord.getY();
        double dZ = entityCoord.getZ() - cameraCoord.getZ();

        double focalLength = 100;
        // E = (eX, eY, eZ) coordinate represents the display surface's position relative to the camera pinhole
        double eX = 0;
        double eY = 0;
        double eZ = focalLength;
        // (bX, bY) position on the 2d screen surface
        double bX = (double) ((eZ / dZ) * dX + eX);
        double bY = (double) ((eZ / dZ) * dY + eY);
        return new Coordinate(bX, bY, 0);
    }

}
