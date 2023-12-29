package util;
import java.awt.Color;

public class Mesh {
    private Color color;
    private Coordinate[] vertices;
    private int numVertices;
    public Mesh(Coordinate[] vertices, Color color) {
        this.color = color;
        this.vertices = vertices;
        this.numVertices = vertices.length;
    }
    public Coordinate[] getVertices() {
        return vertices;
    }

    public Color getColor() {
        return color;
    }

    public int getNumVertices() {
        return numVertices;
    }

    /**
     * Returns the average position on a mesh for computing basic rendering order.
     * @return coordinate representing average position.
     */
    public Coordinate averagePosition() {
        double xSum = 0;
        double ySum = 0;
        double zSum = 0;
        for (Coordinate vertex : vertices) {
            xSum += vertex.getX();
            ySum += vertex.getY();
            zSum += vertex.getZ();
        }
        return new Coordinate(xSum / numVertices, ySum / numVertices, zSum / numVertices);
    }
}
