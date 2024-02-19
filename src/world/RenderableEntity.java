package world;

import util.Mesh;

import java.util.ArrayList;
import java.util.List;

/**
 * Renderable entities are guaranteed to have meshes that can be rendered to the display.
 * */
public class RenderableEntity extends Entity {
    public List<Mesh> meshes;
    public RenderableEntity(double x, double y, double z, double pitch, double yaw, double roll) {
        super(x, y, z, pitch, yaw, roll);
        this.meshes = new ArrayList<>();
    }
    public List<Mesh> getMeshes() {
        return this.meshes;
    }
}
