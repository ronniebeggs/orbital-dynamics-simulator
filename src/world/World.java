package world;

import java.util.HashSet;
import java.util.Set;

public class World {
    // set of all render-able entities contained within the world.
    public Set<Entity> entities;
    public Set<RenderableEntity> renderableEntities;
    public World() {
        this.entities = new HashSet<>();
        this.renderableEntities = new HashSet<>();
    }
    /**
     * Track new entity within the world.
     * @param entity entity to be tracked.
     * */
    public void insertEntity(Entity entity) {
        if (entity instanceof RenderableEntity renderable) {
            renderableEntities.add(renderable);
        }
        entities.add(entity);
    }
    /** Returns all entities within the world. */
    public Set<Entity> fetchEntities() {
        return this.entities;
    }
    /** Returns all renderable entities. */
    public Set<RenderableEntity> fetchRenderableEntities() {
        return this.renderableEntities;
    }
}
