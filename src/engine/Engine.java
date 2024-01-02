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
    public double leadFactor; // ratio of the lead step to the real simulation time step
    public double timeStep; // distance between each calculation (s)
    public double leadStep; // distance between each lead calculation (s)
    public long iterationCounter;

    public void initializeEngine(int displayWidth, int displayHeight, double simulationWidth, double physicsFPS, double leadFactor) {
        this.renderer = new Renderer();
        this.DISPLAY_WIDTH = displayWidth;
        this.DISPLAY_HEIGHT = displayHeight;
        this.simulationWidth = simulationWidth;
        this.scaleFactor = simulationWidth / DISPLAY_WIDTH;

        this.physicFPS = physicsFPS;
        this.multiplierIndex = 0;
        this.timeStep = timeMultiplierOptions[multiplierIndex] / physicFPS;
        this.leadFactor = leadFactor;
        this.leadStep = timeStep * leadFactor;
        this.iterationCounter = 0;
    }

    public void mainLoop() {

        Planet kerbin = new Planet("Kerbin", StdDraw.BLUE, 6378, 5.97 * Math.pow(10, 24));
        Planet mun = new Planet("Mun", kerbin, StdDraw.GRAY, 2737, 0.73 * Math.pow(10, 24), 0.384 * Math.pow(10, 6) / 5, 0, 0);
        Planet duna = new Planet("Duna", kerbin, StdDraw.ORANGE, 2737, 0.5 * Math.pow(10, 24), 0.384 * Math.pow(10, 6) / 3, 0, 0.1*Math.PI);
        Spacecraft spacecraft = new Spacecraft(kerbin, 10, 7878, 0, -0.62 * Math.PI);
        World world = new World(kerbin, spacecraft);

        initializeEngine(600, 600, 300000, 240, 10000);
        renderer.initialize(DISPLAY_WIDTH, DISPLAY_HEIGHT, scaleFactor, world);
        boolean calculateLead = true;
        while (true) {

            if (calculateLead) {
                world.calculateFullLead(leadStep, 1000);
                calculateLead = false;
            } else {
                if (leadFactor > 1) {
                    if (iterationCounter % Math.round(leadFactor) == 0) {
                        world.calculateOneLeadInterval(leadStep);
                        world.removeLeadInterval();
                    }
                } else if (leadFactor < 1) {
                    for (int leadIndex = 0; leadIndex < Math.round(1 / leadFactor); leadIndex++) {
                        world.calculateOneLeadInterval(leadStep);
                        world.removeLeadInterval();
                    }
                } else {
                    world.calculateOneLeadInterval(leadStep);
                    world.removeLeadInterval();
                }
            }
            renderer.renderFrame();

            if (StdDraw.hasNextKeyTyped()) {
                char keyPress = StdDraw.nextKeyTyped();
                calculateLead = handleMovement(spacecraft, keyPress);
            }
            world.updatePlanetMovement(timeStep);
            world.updateSpacecraftMovement(timeStep);

            iterationCounter = (iterationCounter + 1) % Long.MAX_VALUE;
        }
    }
    public boolean handleMovement(Spacecraft spacecraft, char keyPress) {
        boolean recalculateLead = false;
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
            case 'w' -> {
                spacecraft.engageThrust(1, 0.005);
                recalculateLead = true;
            }
            case 's' -> {
                spacecraft.engageThrust(-1, 0.005);
                recalculateLead = true;
            }
        };
        return recalculateLead;
    }
    public void changeTimeMultiplier(int change) {
        int newIndex = multiplierIndex + change;
        if (newIndex >= 0 && newIndex <= timeMultiplierOptions.length - 1) {
            multiplierIndex = newIndex;
            timeStep = timeMultiplierOptions[multiplierIndex] / physicFPS;
            leadFactor = leadStep / timeStep;
        }
    }
}
