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
//    public void singleFrameTest() {
//        Planet center = new Planet(StdDraw.BLUE, 6378, 5.97 * Math.pow(10, 24));
//        World world = new World(center);
//        ter.initialize(DISPLAY_WIDTH, DISPLAY_HEIGHT, (double) 100000 / DISPLAY_WIDTH);
//        ter.renderFrame(world);
//    }

    public void mainLoop() {
        World world = new World();
        Planet planet = new Planet(StdDraw.BLUE, 6378, 5.97 * Math.pow(10, 24));
        Spacecraft spacecraft = new Spacecraft(planet, 10, 0, 0, 0, 0, 7878, 0, 0);

        double[] timeMultiplier = new double[]{1, 10, 100, 1000, 10000};
        double physicsFPS = 1000;
        double timeStep = timeMultiplier[2] / physicsFPS;
        double simulationWidth = 20000; // initial (real) width displayed to the user
        double scaleFactor = simulationWidth / DISPLAY_WIDTH; // number of km displayed per pixel

        world.initializeWorld(planet);
        ter.initialize(DISPLAY_WIDTH, DISPLAY_HEIGHT, scaleFactor);
        while (true) {
            ter.renderFrame(world);
            if (StdDraw.hasNextKeyTyped()) {
                char keyPress = StdDraw.nextKeyTyped();
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
