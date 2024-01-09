package world;

import edu.princeton.cs.algs4.StdDraw;
import util.Coordinate;
import util.Mesh;

import java.util.ArrayList;

public class Cube extends Entity {
    int sideLength;
    public Cube(int x, int y, int z, int sideLength) {
        super(x, y, z, 0, 0, 0);
        this.sideLength = sideLength;
        createCube();
    }
    /**
     * Initialize a basic multicolored cube for testing 3d projections.
     * */
    public void createCube() {
        int halfLength = sideLength / 2;
        // insert vertices clockwise from bottom left corner
        Coordinate vertex1 = new Coordinate(xPosition - halfLength, yPosition - halfLength, zPosition - halfLength);
        Coordinate vertex2 = new Coordinate(xPosition - halfLength, yPosition + halfLength, zPosition - halfLength);
        Coordinate vertex3 = new Coordinate(xPosition + halfLength, yPosition + halfLength, zPosition - halfLength);
        Coordinate vertex4 = new Coordinate(xPosition + halfLength, yPosition - halfLength, zPosition - halfLength);
        Coordinate vertex5 = new Coordinate(xPosition - halfLength, yPosition - halfLength, zPosition + halfLength);
        Coordinate vertex6 = new Coordinate(xPosition - halfLength, yPosition + halfLength, zPosition + halfLength);
        Coordinate vertex7 = new Coordinate(xPosition + halfLength, yPosition + halfLength, zPosition + halfLength);
        Coordinate vertex8 = new Coordinate(xPosition + halfLength, yPosition - halfLength, zPosition + halfLength);
        // create faces of the cube
        meshes = new ArrayList<>();
        Mesh frontFace = new Mesh(new Coordinate[]{vertex1, vertex2, vertex3, vertex4}, StdDraw.RED);
        Mesh topFace = new Mesh(new Coordinate[]{vertex2, vertex6, vertex7, vertex3}, StdDraw.BLUE);
        Mesh bottomFace = new Mesh(new Coordinate[]{vertex1, vertex5, vertex8, vertex4}, StdDraw.GREEN);
        Mesh backFace = new Mesh(new Coordinate[]{vertex5, vertex6, vertex7, vertex8}, StdDraw.YELLOW);
        Mesh leftFace = new Mesh(new Coordinate[]{vertex1, vertex2, vertex6, vertex5}, StdDraw.ORANGE);
        Mesh rightFace = new Mesh(new Coordinate[]{vertex4, vertex3, vertex7, vertex8}, StdDraw.WHITE);
        meshes.add(frontFace);
        meshes.add(topFace);
        meshes.add(bottomFace);
        meshes.add(backFace);
        meshes.add(leftFace);
        meshes.add(rightFace);
    }
}
