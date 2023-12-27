package engine;

import edu.princeton.cs.algs4.StdDraw;
import util.Coordinate;
import world.Entity;
import world.Spacecraft;
import world.World;
import world.Planet;

import java.awt.Color;

public class Renderer {
    private int displayWidth;
    private int displayHeight;

    public void initialize(int width, int height) {
        this.displayWidth = width;
        this.displayHeight = height;

        StdDraw.setCanvasSize(width, height);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);

        StdDraw.clear(new Color(0, 0, 0));
        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }

    /**
     * Clears the display then renders each entity within the world.
     * @param world current world state to be rendered.
     * */
    public void renderFrame(World world) {
        StdDraw.clear(new Color(0, 0, 0));
        StdDraw.enableDoubleBuffering();
        for (Entity entity : world.fetchEntities()) {
            renderEntity(entity);
        }
        StdDraw.show();
    }

    /**
     * Render each of an entity's meshes in decreasing order of distance to the camera.
     * @param entity entity to be rendered.
     * */
    public void renderEntity(Entity entity) {
        if (entity instanceof Planet planet) {
            Coordinate position = planet.getPosition();
            StdDraw.setPenColor(planet.color);
            StdDraw.filledCircle(position.getX() + ((double) displayWidth / 2), position.getY() + ((double) (displayWidth / 2)), planet.radius);
        } else if (entity instanceof Spacecraft spacecraft) {
            Coordinate position = spacecraft.getPosition();
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.filledSquare(position.getX() + ((double) displayWidth / 2), position.getY() + ((double) (displayWidth / 2)), 10);
        }
    }
}


