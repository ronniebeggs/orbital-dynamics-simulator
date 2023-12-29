package world;

import util.Coordinate;
import util.Mesh;
import java.util.List;

public class Entity {
    public double xPosition;
    public double yPosition;
    public double zPosition;
    public List<Mesh> meshes;

    public Entity(double x, double y, double z) {
        xPosition = x;
        yPosition = y;
        zPosition = z;
    }

    public List<Mesh> getMeshes() {
        return this.meshes;
    }
    public Coordinate getPosition() {
        return new Coordinate(xPosition, yPosition, zPosition);
    }
}
