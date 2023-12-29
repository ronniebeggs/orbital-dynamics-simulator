package world;

import java.util.HashSet;
import java.util.Set;

public class World {
    // set of all render-able entities contained within the world.
    public Set<Entity> entities;
    public World() {
        entities = new HashSet<>();
    }

    /**
     * Track new entity within the world.
     * @param entity entity to be tracked.
     * */
    public void insertEntity(Entity entity) {
        entities.add(entity);
    }
    /**
     * Returns all entities to be rendered onto the display.
     * */
    public Set<Entity> fetchEntities() {
        return entities;
    }
}
