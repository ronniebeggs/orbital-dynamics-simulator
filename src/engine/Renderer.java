package engine;

import edu.princeton.cs.algs4.StdDraw;
import util.Coordinate;
import world.*;

import java.awt.Color;

public class Renderer {
    private int displayWidth;
    private int displayHeight;
    private double scaleFactor;
    private int targetIndex;

    public void initialize(int width, int height, double scaleFactor) {
        this.displayWidth = width;
        this.displayHeight = height;
        this.scaleFactor = scaleFactor;

        StdDraw.setCanvasSize(width, height);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);

        StdDraw.clear(new Color(0, 0, 0));
        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }
    public void renderFrame(World world) {
        StdDraw.clear(new Color(0, 0, 0));
        StdDraw.enableDoubleBuffering();
        renderEntity(world.simulationCenter);
        for (Satellite satellite : world.getOrderedChildren()) {
            renderEntity(satellite);
        }
        StdDraw.show();
    }
    public void renderEntity(Entity entity) {
        if (entity instanceof Planet planet) {
            Coordinate position = planet.getPosition();
            StdDraw.setPenColor(planet.color);
            StdDraw.filledCircle(displayPosition(position.getX()), displayPosition(position.getY()), realToDisplayUnits(planet.radius));
        } else if (entity instanceof Spacecraft spacecraft) {
            Coordinate position = spacecraft.getPosition();
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.filledSquare(displayPosition(position.getX()), displayPosition(position.getY()), 10);
        }
    }
    public void changeScaleFactor(double multiplier) {
        scaleFactor *= multiplier;
    }
    private double displayPosition(double realPosition) {
        return ((double) (displayWidth / 2)) + realToDisplayUnits(realPosition);
    }
    private double realToDisplayUnits(double realPosition) {
        return Math.round(realPosition / scaleFactor);
    }
}


