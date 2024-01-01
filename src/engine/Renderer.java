package engine;

import edu.princeton.cs.algs4.StdDraw;
import util.Coordinate;
import world.*;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;

public class Renderer {
    private int displayWidth;
    private int displayHeight;
    private double scaleFactor;
    private List<Satellite> orderedTargetList;
    private Satellite simulationCenter;
    private int targetIndex;
    private Satellite targetSatellite;

    public void initialize(int width, int height, double scaleFactor, World world) {
        this.displayWidth = width;
        this.displayHeight = height;
        this.scaleFactor = scaleFactor;
        this.orderedTargetList = world.getOrderedChildren();
        this.simulationCenter = world.getSimulationCenter();
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
        for (Satellite satellite : orderedTargetList) {
            if (satellite instanceof Planet planet) {
                if (!planet.equals(simulationCenter)) {
                    drawPlanetLead(planet);
                }
                renderPlanet(planet);
            } else if (satellite instanceof Spacecraft spacecraft) {
                drawSpacecraftLead(spacecraft);
                renderSpacecraft(spacecraft);
            }
        }
        StdDraw.show();
    }
    public void renderPlanet(Planet planet) {
        Coordinate displayPosition = transformToDisplay(planet.getPosition());
        StdDraw.setPenColor(planet.color);
        StdDraw.filledCircle(displayPosition.getX(), displayPosition.getY(), realToDisplayUnits(planet.radius));
    }
    public void renderSpacecraft(Spacecraft spacecraft) {
        Coordinate displayPosition = transformToDisplay(spacecraft.getPosition());
        double displayX = displayPosition.getX();
        double displayY = displayPosition.getY();
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.filledCircle(displayX, displayY, realToDisplayUnits(spacecraft.shipSize));
        StdDraw.setPenColor(StdDraw.GREEN);
        StdDraw.filledPolygon(
                new double[]{displayX, displayX - 5, displayX + 5},
                new double[]{displayY, displayY + 10, displayY + 10}
        );
    }
    public void drawPlanetLead(Planet planet) {
        StdDraw.setPenColor(planet.color);
        for (int degree = 0; degree < 360; degree++) {
            Coordinate parentPosition = planet.parent.getPosition();

            double xPosition = parentPosition.getX() + planet.orbitalRadius * Math.cos(Math.toRadians(degree));
            double yPosition = parentPosition.getY() + planet.orbitalRadius * Math.sin(Math.toRadians(degree));

            Coordinate displayPosition = transformToDisplay(new Coordinate(xPosition, yPosition));
            StdDraw.filledCircle(displayPosition.getX(), displayPosition.getY(), 1);
        }
    }
    public void drawSpacecraftLead(Spacecraft spacecraft) {

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
    private Coordinate transformToDisplay(Coordinate realPosition) {
        return new Coordinate(
                ((double) (displayWidth / 2)) - realToDisplayUnits(targetSatellite.getPosition().getX()) + realToDisplayUnits(realPosition.getX()),
                ((double) (displayHeight / 2)) - realToDisplayUnits(targetSatellite.getPosition().getY()) + realToDisplayUnits(realPosition.getY())
        );
    }
    private double realToDisplayUnits(double realPosition) {
        return Math.round(realPosition / scaleFactor);
    }
}


