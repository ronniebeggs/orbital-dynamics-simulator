package engine;

import edu.princeton.cs.algs4.StdDraw;
import world.Planet;
import world.Spacecraft;
import world.World;

public class Engine {
    Renderer renderer = new Renderer();
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
        Planet kerbin = new Planet("Kerbin", StdDraw.BLUE, 6378, 5.97 * Math.pow(10, 24));
        Planet mun = new Planet("Mun", StdDraw.GRAY, kerbin, 2737, 0.73 * Math.pow(10, 24), 0.384 * Math.pow(10, 6) / 5, 0);
        Planet duna = new Planet("Duna", StdDraw.ORANGE, kerbin, 2737, 0.5 * Math.pow(10, 24), 0.384 * Math.pow(10, 6) / 3, -0.1*Math.PI);

        double[] timeMultiplier = new double[]{1, 10, 100, 1000, 10000};
        double physicsFPS = 1000;
        double timeStep = timeMultiplier[4] / physicsFPS;
        double simulationWidth = 300000; // initial (real) width displayed to the user
        double scaleFactor = simulationWidth / DISPLAY_WIDTH; // number of km displayed per pixel

        world.initializeWorld(kerbin);
        renderer.initialize(DISPLAY_WIDTH, DISPLAY_HEIGHT, scaleFactor);
        while (true) {
            renderer.renderFrame(world);
            if (StdDraw.hasNextKeyTyped()) {
                char keyPress = StdDraw.nextKeyTyped();
                handleMovement(keyPress);
            }
            world.updatePlanetMovement(timeStep);
//            world.updateSpacecraftMovement(timeStep, spacecraft);
        }
    }

    public void handleMovement(char keyPress) {
        switch (keyPress) {
            case '1' -> {
                renderer.changeScaleFactor(0.5);
            }
            case '2' -> {
                renderer.changeScaleFactor(1.5);
            }
        };
    }
}
