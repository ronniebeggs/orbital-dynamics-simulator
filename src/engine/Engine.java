package engine;

import world.Planet;
import world.World;

import java.awt.*;

public class Engine {
    Renderer ter = new Renderer();
    public final int DISPLAY_WIDTH = 600;
    public final int DISPLAY_HEIGHT = 600;
    /**
     * Test function for rendering individual scenes.
     * */
    public void singleFrameTest() {
        World world = new World();
        Planet planet = new Planet(null, 100, Color.BLUE, 100, 0, 0, 0, 0, 0, 0, 0);
        world.insertEntity(planet);
        ter.initialize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
        ter.renderFrame(world);
    }
}
