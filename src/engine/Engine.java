package engine;

import edu.princeton.cs.algs4.StdDraw;
import world.Planet;
import world.Spacecraft;
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
        ter.initialize(DISPLAY_WIDTH, DISPLAY_HEIGHT, (double) 1000000 / DISPLAY_WIDTH);
        ter.renderFrame(world);
    }

    public void mainLoop() {
        World world = new World();
        Planet planet = new Planet(null, 6378, Color.BLUE, 5.97 * Math.pow(10, 24), 0, 0, 0, 0, 0, 0, 0);
        Spacecraft spacecraft = new Spacecraft(planet, 10, 7878, 0, 0, 0, 0, 0, 0);
        world.insertEntity(planet);
        world.insertEntity(spacecraft);
        double physicsFPS = 240;
        double timeStep = 1 / physicsFPS;
        double simulationWidth = 20000; // initial (real) width displayed to the user
        double scaleFactor = simulationWidth / DISPLAY_WIDTH; // number of km displayed per pixel
        ter.initialize(DISPLAY_WIDTH, DISPLAY_HEIGHT, scaleFactor);
        while (true) {
            ter.renderFrame(world);
            if (StdDraw.hasNextKeyTyped()) {
                char keyPress = StdDraw.nextKeyTyped();
                boolean isTargetLocked = true;
                handleMovement(keyPress);
            }
            world.updatePlanetMovement(timeStep);
            world.updateSpacecraftMovement(timeStep, spacecraft);
        }
    }

    public void handleMovement(char keyPress) {
//        switch (keyPress) {
//            case 'u' -> {
//                camera.pointToward(target);
//            }
//        };
    }
}
