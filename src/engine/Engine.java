package engine;

import edu.princeton.cs.algs4.StdDraw;
import world.Camera;
import world.Planet;
import world.Spacecraft;
import world.World;

public class Engine {
    public Renderer renderer;
    public final double[] timeMultiplierOptions = new double[]{1, 10, 100, 1000, 10000};
    public int multiplierIndex; // index deciding which of the time multiplier options control the simulation
    public double physicFPS; // number of physics frames computed every second
    public double timeStep; // distance between each calculation (s)
    public double leadStep; // distance between each lead calculation (s)
    public double leadFactor; // ratio of the lead step to the real simulation time step
    public long iterationCounter; // counts the number of iterations that occur (also the # of frames rendered)

    public void initializeEngine(double physicsFPS, double leadFactor) {
        this.renderer = new Renderer();
        this.physicFPS = physicsFPS;
        this.multiplierIndex = 0;
        this.timeStep = timeMultiplierOptions[multiplierIndex] / physicFPS;
        this.leadFactor = leadFactor;
        this.leadStep = timeStep * leadFactor;
        this.iterationCounter = 0;
    }

    /**
     * Main engine loop for the 2D simulation logic.
     * */
    public void mainLoop() {

        Planet kerbin = new Planet("Kerbin", StdDraw.BLUE, 6378, 5.97 * Math.pow(10, 24));
        Planet mun = new Planet("Mun", kerbin, StdDraw.GRAY, 1737, 0.73 * Math.pow(10, 24), 0.384 * Math.pow(10, 6) / 5, 0, 0);
        Planet duna = new Planet("Duna", kerbin, StdDraw.ORANGE, 2737, 0.5 * Math.pow(10, 24), 0.384 * Math.pow(10, 6) / 3, 0, 0.1*Math.PI);
        Spacecraft spacecraft = new Spacecraft(kerbin, StdDraw.RED, 10, 7878, 0, -0.62 * Math.PI);
        Camera camera = new Camera(spacecraft, 1000);
        World world = new World(kerbin, spacecraft, camera);

        int physicsFPS = 240;
        int leadFactor = 10000;
        int displayWidth = 800;
        int displayHeight = 800;
        int initialSimulationWidth = 300000;
        int scaleFactor = initialSimulationWidth / displayWidth;

        initializeEngine(physicsFPS, leadFactor);
        renderer.initialize(displayWidth, displayHeight, scaleFactor, world.getSimulationCenter(), world.getOrderedChildren());

        boolean calculateLead = true; // determines whether to (re)calculate the full lead during the following iteration
        while (true) {
            performLeadCalculations(world, calculateLead);
            renderer.renderFrame();
            // handle movement controls
            boolean thrustEngaged = false; // must re-calculate lead if thrust controls engaged
            if (StdDraw.hasNextKeyTyped()) {
                thrustEngaged = handleMovement(spacecraft, StdDraw.nextKeyTyped());
            }
            world.updatePlanetMovement(timeStep);
            // must recalculate lead if the spacecraft's parent changes
            boolean parentChanged = world.updateSpacecraftMovement(timeStep);
            // lead calculated with larger time step, so lead will sometimes drift away from spacecraft -> must recalculate lead when this occurs
            boolean leadDrift = spacecraft.distanceToFirstLead() > 500;
            calculateLead = thrustEngaged || parentChanged || leadDrift;
            // increment iteration counter; ensure it doesn't exceed max value
            iterationCounter = (iterationCounter + 1) % Long.MAX_VALUE;
        }
    }
    /**
     * Perform lead calculations during each iteration.
     * After calculating the entire lead from scratch, it'll continue to manipulate lead position/velocity deques for each satellite.
     * @param calculateLead boolean determining whether to recalculate the full lead.
     * */
    public void performLeadCalculations(World world, boolean calculateLead) {
        if (calculateLead) {
            world.calculateFullLead(leadStep, 1000);
        } else {
            // (leadStep > timeStep) : lead positions created less frequently so spacecraft can catch up
            if (leadFactor > 1) {
                if (iterationCounter % Math.round(leadFactor) == 0) {
                    world.calculateOneLeadInterval(leadStep);
                    world.removeLeadInterval();
                }
            // (leadStep < timeStep) : multiple lead calculations performed during each simulation iteration for more precise leads
            } else if (leadFactor < 1) {
                for (int leadIndex = 0; leadIndex < Math.round(1 / leadFactor); leadIndex++) {
                    world.calculateOneLeadInterval(leadStep);
                    world.removeLeadInterval();
                }
            // (leadStep == timeStep) : lead positions calculated at the same rate as simulation calculations
            } else {
                world.calculateOneLeadInterval(leadStep);
                world.removeLeadInterval();
            }
        }
    }
    /**
     * Handles all user inputs and triggers the corresponding behavior.
     * @param keyPress character input from the `StdDraw` key press queue.
     * @return boolean indicating if thrust controls were initiated by users; must trigger a lead re-calculation.
     * */
    public boolean handleMovement(Spacecraft spacecraft, char keyPress) {
        boolean thrustEngaged = false;
        switch (keyPress) {
            // ZOOM IN/OUT:
            case '1' -> {
                renderer.changeScaleFactor(0.5);
            }
            case '2' -> {
                renderer.changeScaleFactor(1.5);
            }
            // CHANGE CURRENT SIMULATION TARGET:
            case 'q' -> {
                renderer.changeTargetIndex(1);
            }
            case 'e' -> {
                renderer.changeTargetIndex(-1);
            }
            // CHANGE SIMULATION SPEED (speed up/slow down orbital calculations)
            case 'z' -> {
                changeTimeMultiplier(1);
            }
            case 'x' -> {
                changeTimeMultiplier(-1);
            }
            // ENGAGE SPACECRAFT THRUST:
            case 'w' -> {
                spacecraft.engageThrust(1, 0.005);
                thrustEngaged = true;
            }
            case 's' -> {
                spacecraft.engageThrust(-1, 0.005);
                thrustEngaged = true;
            }
        };
        return thrustEngaged;
    }
    /**
     * Changes simulation speed by adjusting the `timeStep` and `leadFactor`.
     * Longer timeSteps give the appearance of a faster simulation, at the expense calculation precision.
     * @param change changes which of the time multiplier options is selected.
     * */
    public void changeTimeMultiplier(int change) {
        int newIndex = multiplierIndex + change;
        if (newIndex >= 0 && newIndex <= timeMultiplierOptions.length - 1) {
            multiplierIndex = newIndex;
            timeStep = timeMultiplierOptions[multiplierIndex] / physicFPS;
            leadFactor = leadStep / timeStep;
        }
    }
}
