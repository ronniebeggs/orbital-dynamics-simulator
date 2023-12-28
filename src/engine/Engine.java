package engine;

import edu.princeton.cs.algs4.StdDraw;
import util.PlanetBuilder;
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
        PlanetBuilder kerbin = new PlanetBuilder("Kerbin", StdDraw.BLUE, 6378, 5.97 * Math.pow(10, 24));
        PlanetBuilder mun = new PlanetBuilder("Mun", kerbin, StdDraw.GRAY, 2737, 0.73 * Math.pow(10, 24), 0.384 * Math.pow(10, 6) / 5, 0);
        PlanetBuilder duna = new PlanetBuilder("Duna", kerbin, StdDraw.ORANGE, 2737, 0.5 * Math.pow(10, 24), 0.384 * Math.pow(10, 6) / 3, -0.1*Math.PI);

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
