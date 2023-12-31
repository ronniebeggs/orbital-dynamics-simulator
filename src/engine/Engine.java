package engine;

import edu.princeton.cs.algs4.StdDraw;
import util.PlanetBuilder;
import world.Planet;
import world.Spacecraft;
import world.World;

public class Engine {
    public Renderer renderer;
    public int DISPLAY_WIDTH;
    public int DISPLAY_HEIGHT;
    public final double[] timeMultiplierOptions = new double[]{1, 10, 100, 1000, 10000};
    public int multiplierIndex;
    public double simulationWidth; // initial (real) width displayed to the user
    public double scaleFactor; // number of km displayed per pixel
    public double physicFPS; // number of physics frames computed every second
    public double timeStep; // distance between each calculation (s)

    public void initializeEngine(int displayWidth, int displayHeight, double simulationWidth, double physicsFPS) {
        this.DISPLAY_WIDTH = displayWidth;
        this.DISPLAY_HEIGHT = displayHeight;
        this.renderer = new Renderer();

        this.simulationWidth = simulationWidth;
        this.physicFPS = physicsFPS;
        this.multiplierIndex = 0;
        setTimeStep(multiplierIndex);
        this.scaleFactor = simulationWidth / DISPLAY_WIDTH;
    }

    public void mainLoop() {
        World world = new World();
//        PlanetBuilder kerbin = new PlanetBuilder("Kerbin", StdDraw.BLUE, 6378, 5.97 * Math.pow(10, 24));
//        PlanetBuilder mun = new PlanetBuilder("Mun", kerbin, StdDraw.GRAY, 2737, 0.73 * Math.pow(10, 24), 0.384 * Math.pow(10, 6) / 5, 0);
//        PlanetBuilder duna = new PlanetBuilder("Duna", kerbin, StdDraw.ORANGE, 2737, 0.5 * Math.pow(10, 24), 0.384 * Math.pow(10, 6) / 3, -0.1*Math.PI);

        Planet kerbin = new Planet("Kerbin", null, StdDraw.BLUE, 6378, 5.97 * Math.pow(10, 24));
        Planet mun = new Planet("Mun", kerbin, StdDraw.GRAY, 2737, 0.73 * Math.pow(10, 24), 0.384 * Math.pow(10, 6) / 5, 0, 0);
        Planet duna = new Planet("Duna", kerbin, StdDraw.ORANGE, 2737, 0.5 * Math.pow(10, 24), 0.384 * Math.pow(10, 6) / 3, 0, 0.1*Math.PI);
        Spacecraft craft = new Spacecraft(kerbin, 10, 7878, 0, 0);

        initializeEngine(600, 600, 300000, 240);
        world.initializeWorld(kerbin);
        renderer.initialize(DISPLAY_WIDTH, DISPLAY_HEIGHT, scaleFactor, world.getOrderedSatellites());
        while (true) {
            renderer.renderFrame();
            if (StdDraw.hasNextKeyTyped()) {
                char keyPress = StdDraw.nextKeyTyped();
                handleMovement(keyPress);
            }
            world.updatePlanetMovement(timeStep);
            world.updateSpacecraftMovement(timeStep, craft);
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
            case 'q' -> {
                renderer.changeTargetIndex(1);
            }
            case 'e' -> {
                renderer.changeTargetIndex(-1);
            }
            case 'z' -> {
                changeTimeMultiplier(1);
            }
            case 'x' -> {
                changeTimeMultiplier(-1);
            }
        };
    }
    public void setTimeStep(int multiplierIndex) {
        timeStep = timeMultiplierOptions[multiplierIndex] / physicFPS;
    }
    public void changeTimeMultiplier(int change) {
        int newIndex = multiplierIndex + change;
        if (newIndex >= 0 && newIndex <= timeMultiplierOptions.length - 1) {
            multiplierIndex = newIndex;
            setTimeStep(multiplierIndex);
        }
    }
}
