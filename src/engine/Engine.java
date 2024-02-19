package engine;

import edu.princeton.cs.algs4.StdDraw;
import world.*;
import world.assets.Sphere;

/**
 * Class that handles the overarching operations of the project.
 * Solicits user input, captures the world state, and renders each frame.
 * */
public class Engine {
    Renderer ter = new Renderer();
    Camera camera;
    public final int DISPLAY_WIDTH = 600;
    public final int DISPLAY_HEIGHT = 600;
    public final int VERTICAL_VIEW_ANGLE = 60;

    /**
     * Test function for rendering individual scenes.
     * */
    public void singleFrameTest() {
        World world = new World();
        camera = new Camera(0, 0, -100, 0, 0, 0);
        Sphere sphere = new Sphere(0, 0, 0, 0, 180, 0, 50, 24, 12);
        world.insertEntity(sphere);
        ter.initialize(camera, null, DISPLAY_WIDTH, DISPLAY_HEIGHT, VERTICAL_VIEW_ANGLE);
        ter.renderFrame(world);
    }

    public void mainLoop() {
        World world = new World();
        camera = new Camera(-100, 0, 0, 0, 0, 0);
        Entity light = new Entity(-1000, 0, 0, 0, 0, 0);
        Entity[] lightSources = new Entity[]{light};
        Sphere sphere = new Sphere(0, 0, 0, 0, 180, 0, 50, 48, 24);
        world.insertEntity(sphere);
        ter.initialize(camera, lightSources, DISPLAY_WIDTH, DISPLAY_HEIGHT, VERTICAL_VIEW_ANGLE);
        while (true) {
            ter.renderFrame(world);
            if (StdDraw.hasNextKeyTyped()) {
                char keyPress = StdDraw.nextKeyTyped();
                boolean isTargetLocked = true;
                if (isTargetLocked) {
                    fixedOrbitalMovement(sphere, keyPress);
                } else {
                    freeMovement(sphere, keyPress);
                }
            }
        }
    }
    public void freeMovement(Entity target, char keyPress) {
        switch (keyPress) {
            case 'u' -> {
                camera.pointToward(target);
            }
            case 'w' -> {
                camera.moveFrontal(10);
            }
            case 's' -> {
                camera.moveFrontal(-10);
            }
            case 'd' -> {
                camera.moveLateral(10);
            }
            case 'a' -> {
                camera.moveLateral(-10);
            }
            case 'q' -> {
                camera.moveTransverse(10);
            }
            case 'e' -> {
                camera.moveTransverse(-10);
            }
            case 'i' -> {
                camera.rotatePitch(10);
            }
            case 'k' -> {
                camera.rotatePitch(-10);
            }
            case 'l' -> {
                camera.rotateYaw(-10);
            }
            case 'j' -> {
                camera.rotateYaw(10);
            }
        };
    }
    public void fixedOrbitalMovement(Entity target, char keyPress) {
        switch (keyPress) {
            case 'w' -> {
                camera.moveTowardTarget(target, 10);
            }
            case 's' -> {
                camera.moveTowardTarget(target, -10);
            }
            case 'd' -> {
                camera.rotateAroundHorizontal(target, 10);
            }
            case 'a' -> {
                camera.rotateAroundHorizontal(target, -10);
            }
            case 'q' -> {
                camera.rotateAroundVertical(target, 10);
            }
            case 'e' -> {
                camera.rotateAroundVertical(target, -10);
            }
            case 'i' -> {
                camera.rotatePitch(10);
            }
            case 'k' -> {
                camera.rotatePitch(-10);
            }
            case 'l' -> {
                camera.rotateYaw(-10);
            }
            case 'j' -> {
                camera.rotateYaw(10);
            }
        }
    }
}
