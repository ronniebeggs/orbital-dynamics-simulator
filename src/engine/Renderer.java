package engine;

import edu.princeton.cs.algs4.StdDraw;
import util.Coordinate;
import world.*;

import java.awt.Color;
import java.util.List;

public class Renderer {
    private int displayWidth;
    private int displayHeight;
    private double scaleFactor;
    private List<Satellite> orderedTargetList;
    private int targetIndex;
    private Satellite targetSatellite;

    public void initialize(int width, int height, double scaleFactor, List<Satellite> targets) {
        this.displayWidth = width;
        this.displayHeight = height;
        this.scaleFactor = scaleFactor;
        this.orderedTargetList = targets;
        this.targetIndex = 0;
        this.targetSatellite = orderedTargetList.get(targetIndex);

        StdDraw.setCanvasSize(width, height);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);

        StdDraw.clear(new Color(0, 0, 0));
        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }
    public void renderFrame() {
        StdDraw.clear(new Color(0, 0, 0));
        StdDraw.enableDoubleBuffering();
        for (Satellite satellite : this.orderedTargetList) {
            renderEntity(satellite);
        }
        StdDraw.show();
    }
    public void renderEntity(Entity entity) {
        if (entity instanceof Planet planet) {
            Coordinate position = planet.getPosition();
            StdDraw.setPenColor(planet.color);
            StdDraw.filledCircle(displayPositionX(position.getX()), displayPositionY(position.getY()), realToDisplayUnits(planet.radius));
        } else if (entity instanceof Spacecraft spacecraft) {
            Coordinate position = spacecraft.getPosition();
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.filledSquare(displayPositionX(position.getX()), displayPositionY(position.getY()), 10);
        }
    }
    public void changeScaleFactor(double multiplier) {
        scaleFactor *= multiplier;
    }
    public void setTargetIndex(int newIndex) {
        targetIndex = newIndex;
        targetSatellite = orderedTargetList.get(targetIndex);
    }
    public void changeTargetIndex(int indexChange) {
        targetIndex = (targetIndex + indexChange + orderedTargetList.size()) % orderedTargetList.size();
        targetSatellite = orderedTargetList.get(targetIndex);
    }
    private double displayPositionX(double realXPosition) {
        return ((double) (displayWidth / 2)) - realToDisplayUnits(targetSatellite.getPosition().getX()) + realToDisplayUnits(realXPosition);
    }
    private double displayPositionY(double realYPosition) {
        return ((double) (displayHeight / 2)) - realToDisplayUnits(targetSatellite.getPosition().getY()) + realToDisplayUnits(realYPosition);
    }
    private double realToDisplayUnits(double realPosition) {
        return Math.round(realPosition / scaleFactor);
    }
}


