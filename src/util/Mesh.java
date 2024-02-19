package util;
import world.RenderableEntity;

import java.awt.Color;

public class Mesh {
    private RenderableEntity parent;
    private Color color;
    private Coordinate[] vertices;
    private Coordinate normalVector;
    private int numVertices;
    public Mesh(RenderableEntity parent, Coordinate[] vertices, Color color) {
        this.parent = parent;
        this.color = color;
        this.vertices = vertices;
        this.numVertices = vertices.length;
        this.normalVector = computeNormal(vertices);
    }
    public RenderableEntity getParent() {
        return parent;
    }
    public Coordinate[] getVertices() {
        return vertices;
    }
    public Coordinate getNormalVector() { return normalVector; }
    public Color getColor() {
        return color;
    }
    public int getNumVertices() {
        return numVertices;
    }
    public Coordinate computeNormal(Coordinate[] vertices) {
        if (numVertices >= 3) {
            Coordinate relativePoint = vertices[0];
            Coordinate v1 = vertices[1];
            Coordinate v2 = vertices[2];

            Coordinate relativeV1 = new Coordinate(
                    v1.getX() - relativePoint.getX(),
                    v1.getY() - relativePoint.getY(),
                    v1.getZ() - relativePoint.getZ()
            );

            Coordinate relativeV2 = new Coordinate(
                    v2.getX() - relativePoint.getX(),
                    v2.getY() - relativePoint.getY(),
                    v2.getZ() - relativePoint.getZ()
            );

            Coordinate crossProduct = Coordinate.crossProduct(relativeV1, relativeV2);
            return Coordinate.normalize(crossProduct);
        }
        return null;
    }

    public void changeColorBrightness(double proportion) {
        float[] colorComponents = new float[3];
        this.color.getColorComponents(colorComponents);
        this.color = new Color(
                (int) (colorComponents[0] * proportion),
                (int) (colorComponents[1] * proportion),
                (int) (colorComponents[2] * proportion)
        );
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
