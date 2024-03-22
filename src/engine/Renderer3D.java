package engine;

import edu.princeton.cs.algs4.StdDraw;
import util.Coordinate;
import world.*;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;

public class Renderer3D {
    private int displayWidth;
    private int displayHeight;
    private double scaleFactor; // number of kilometers displayed per pixel
    private int targetIndex; // index tracking the target satellite
    private Camera camera;
    private Satellite simulationCenter;
    private List<Satellite> orderedTargetList;
    private Satellite targetSatellite;

    public void initialize(int width, int height, double scaleFactor, Camera camera, Satellite simulationCenter, List<Satellite> orderedChildren) {
        this.displayWidth = width;
        this.displayHeight = height;
        this.scaleFactor = scaleFactor;
        this.camera = camera;
        this.simulationCenter = simulationCenter;
        this.orderedTargetList = orderedChildren;
        this.targetIndex = 0;
        this.targetSatellite = orderedTargetList.get(targetIndex);

        StdDraw.setCanvasSize(width, height);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);

        StdDraw.clear(new Color(0, 0, 0));
        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }
    /**
     * Clears screen then renders all satellites and their lead positions.
     * Renders entities in an order that places children above their parents.
     * */
    public void renderFrame() {
        StdDraw.clear(new Color(0, 0, 0));
        StdDraw.enableDoubleBuffering();
        // iterate through each satellite and trigger corresponding render method based on satellite type
        for (Satellite satellite : orderedTargetList) {
            if (satellite instanceof Planet planet) {
                renderPlanet(planet);
            } else if (satellite instanceof Spacecraft spacecraft) {
                renderSpacecraft(spacecraft);
            }
//            // render a satellite marker with constant size regardless of zoom
//            if (satellite.equals(targetSatellite)) {
//                renderSatelliteMarker(satellite.getPosition(), StdDraw.GREEN);
//            } else {
//                renderSatelliteMarker(satellite.getPosition(), StdDraw.PRINCETON_ORANGE);
//            }
        }
        StdDraw.show();
    }
    /**
     * Method for rendering planet objects.
     * @param planet specified planet instance.
     * */
    public void renderPlanet(Planet planet) {
        Coordinate displayPosition = transformToDisplay(planet.getPosition());
        StdDraw.setPenColor(planet.color);
        StdDraw.filledCircle(displayPosition.getX(), displayPosition.getY(), realToDisplayUnits(planet.radius));
    }
    /**
     * Method for rendering spacecraft objects.
     * @param spacecraft specified spacecraft instance.
     * */
    public void renderSpacecraft(Spacecraft spacecraft) {
        StdDraw.setPenColor(StdDraw.RED);
        Coordinate displayPosition = transformToDisplay(spacecraft.getPosition());
        StdDraw.filledCircle(displayPosition.getX(), displayPosition.getY(), realToDisplayUnits(spacecraft.shipSize));
    }
    /**
     * Renders a triangular marker so objects are easier to locate.
     * @param realPosition position to place marker.
     * @param color marker color.
     * */
    public void renderSatelliteMarker(Coordinate realPosition, Color color) {
        StdDraw.setPenColor(color);
        Coordinate displayPosition = transformToDisplay(realPosition);
        StdDraw.filledPolygon(
                new double[]{displayPosition.getX(), displayPosition.getX() - 5, displayPosition.getX() + 5},
                new double[]{displayPosition.getY(), displayPosition.getY() + 10, displayPosition.getY() + 10}
        );
    }
    public void renderCamera() {
        StdDraw.setPenColor(StdDraw.MAGENTA);
        double viewLineDistance = 1000;
        Coordinate cameraPosition = camera.getPosition();
        Coordinate cameraViewEndpoint = new Coordinate(
                cameraPosition.getX() + viewLineDistance * Math.cos(camera.getAbsoluteDirection()),
                cameraPosition.getY() + viewLineDistance * Math.sin(camera.getAbsoluteDirection())
        );
        Coordinate displayPosition = transformToDisplay(camera.getPosition());
        Coordinate endpointDisplay = transformToDisplay(cameraViewEndpoint);
        StdDraw.filledCircle(displayPosition.getX(), displayPosition.getY(), 3);
        StdDraw.line(
                displayPosition.getX(), displayPosition.getY(),
                endpointDisplay.getX(), endpointDisplay.getY()
        );

    }
    /**
     * Draws the circular path that the inputted planet will follow in its orbit.
     * @param planet specified planet instance.
     * */
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
    /**
     * Renders each of the spacecraft's lead positions onto the display.
     * Will render lead positions relative to an orbiting parent planet, so that the lead appears circular.
     * Otherwise, it will render the absolute lead positions.
     * @param spacecraft specified spacecraft instance.
     */
    public void drawSpacecraftLead(Spacecraft spacecraft) {
        if (spacecraft.parent == null) {
            StdDraw.setPenColor(spacecraft.color);
            // transform and render absolute lead positions
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
                // calculate the spacecraft's lead position relative to its parent's future position
                double distanceToParent = parentLeadPosition.distanceTo(spacecraftLeadPosition);
                double angleBetween = parentLeadPosition.angleBetween(spacecraftLeadPosition);
                // predict how the lead position will appear relative to its parent future position
                Coordinate positionRelativeToParent = new Coordinate(
                        parent.getPosition().getX() + distanceToParent * Math.cos(angleBetween),
                        parent.getPosition().getY() + distanceToParent * Math.sin(angleBetween)
                );
                Coordinate displayPosition = transformToDisplay(positionRelativeToParent);
                StdDraw.filledCircle(displayPosition.getX(), displayPosition.getY(), 1);
            }
        }
    }
    /**
     * Changes the real distance : display distance ratio to produce zoom effects.
     * @param multiplier factor to multiply the current `scaleFactor` by.
     * */
    public void changeScaleFactor(double multiplier) {
        scaleFactor *= multiplier;
    }
    /**
     * Shift the index which decides which of the satellites will be centrally displayed.
     * @param indexChange value to shift the current index (will wrap around).
     * */
    public void changeTargetIndex(int indexChange) {
        targetIndex = (targetIndex + indexChange + orderedTargetList.size()) % orderedTargetList.size();
        targetSatellite = orderedTargetList.get(targetIndex);
    }
    /**
     * Transform a position within the simulation to a display coordinate.
     * @param realPosition position to be transformed (km).
     * @return resulting position relative to the display (display pixels).
     * */
    private Coordinate transformToDisplay(Coordinate realPosition) {
        return new Coordinate(
                ((double) (displayWidth / 2)) - realToDisplayUnits(targetSatellite.getPosition().getX()) + realToDisplayUnits(realPosition.getX()),
                ((double) (displayHeight / 2)) - realToDisplayUnits(targetSatellite.getPosition().getY()) + realToDisplayUnits(realPosition.getY())
        );
    }
    /**
     * Scale simulation distances to display distances.
     * @param realPosition simulation distance to be scaled (km).
     * @return resulting distance relative to the display (display pixels).
     * */
    private double realToDisplayUnits(double realPosition) {
        return Math.round(realPosition / scaleFactor);
    }
}


