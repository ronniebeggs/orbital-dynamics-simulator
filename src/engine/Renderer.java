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
                renderSatelliteMarker(planet.getPosition(), StdDraw.PRINCETON_ORANGE);
            } else if (satellite instanceof Spacecraft spacecraft) {
                drawSpacecraftLead(spacecraft);
                renderSpacecraft(spacecraft);
                renderSatelliteMarker(spacecraft.getPosition(), StdDraw.GREEN);
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
        StdDraw.setPenColor(StdDraw.RED);
        Coordinate displayPosition = transformToDisplay(spacecraft.getPosition());
        StdDraw.filledCircle(displayPosition.getX(), displayPosition.getY(), realToDisplayUnits(spacecraft.shipSize));
    }
    public void renderSatelliteMarker(Coordinate realPosition, Color color) {
        StdDraw.setPenColor(color);
        Coordinate displayPosition = transformToDisplay(realPosition);
        StdDraw.filledPolygon(
                new double[]{displayPosition.getX(), displayPosition.getX() - 5, displayPosition.getX() + 5},
                new double[]{displayPosition.getY(), displayPosition.getY() + 10, displayPosition.getY() + 10}
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
        if (spacecraft.parent == null) {
            StdDraw.setPenColor(spacecraft.color);
            for (Coordinate leadPosition : spacecraft.getLeadPositions()) {
                Coordinate displayPosition = transformToDisplay(leadPosition);
                StdDraw.filledCircle(displayPosition.getX(), displayPosition.getY(), 1);
            }
        } else {
            Satellite parent = spacecraft.parent;
            StdDraw.setPenColor(parent.color);
            Iterator<Coordinate> spacecraftLeads = spacecraft.getLeadPositions().iterator();
            Iterator<Coordinate> parentLeads = parent.getLeadPositions().iterator();
            while (spacecraftLeads.hasNext() && parentLeads.hasNext()) {
                Coordinate spacecraftLeadPosition = spacecraftLeads.next();
                Coordinate parentLeadPosition = parentLeads.next();

                double distanceToParent = spacecraftLeadPosition.distanceTo(parentLeadPosition);
                double angleBetween = spacecraftLeadPosition.angleBetween(parentLeadPosition);

                Coordinate positionRelativeToParent = new Coordinate(
                        parent.getPosition().getX() + distanceToParent * Math.cos(angleBetween),
                        parent.getPosition().getY() + distanceToParent * Math.sin(angleBetween)
                );
                Coordinate displayPosition = transformToDisplay(positionRelativeToParent);
                StdDraw.filledCircle(displayPosition.getX(), displayPosition.getY(), 1);
            }
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


