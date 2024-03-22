package engine;

import edu.princeton.cs.algs4.StdDraw;
import util.Coordinate;
import util.Mesh;
import util.Transformations;
import world.*;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

public class Renderer3D {
    /**
     * `MeshRankNode` allows us the rank meshes based on proximity to the camera.
     * Mitigates awkward overlap of different meshes.
     * */
    public static class MeshRankNode implements Comparable {
        double distance;
        Mesh mesh;
        public MeshRankNode(Mesh mesh, double distance) {
            this.mesh = mesh;
            this.distance = distance;
        }
        @Override
        public int compareTo(Object o) {
            if (o instanceof MeshRankNode other) {
                return Double.compare(other.distance, this.distance);
            }
            throw new IllegalArgumentException();
        }

    }
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
            }
//            else if (satellite instanceof Spacecraft spacecraft) {
//                renderSpacecraft(spacecraft);
//            }
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
        // map each mesh to its distance relative to the camera, and place within priority queue.
        PriorityQueue<MeshRankNode> meshRank = new PriorityQueue<>();
        for (Mesh mesh : planet.getMeshes()) {
            Coordinate meshPosition = mesh.averagePosition();
            Coordinate cameraPosition = camera.getPosition();
            double distanceToCamera = cameraPosition.distance3D(meshPosition);
            meshRank.add(new MeshRankNode(mesh, distanceToCamera));
        }
        // render each mesh in decreasing order relative to the camera to mitigate rendering overlap.
        while (!meshRank.isEmpty()) {
            renderMesh(meshRank.remove().mesh);
        }
    }

    /**
     * Draw and fill the mesh using StdDraw library.
     * @param mesh mesh to be rendered.
     * */
    public void renderMesh(Mesh mesh) {
//        Color adjustedColor = shadeMesh(mesh);
        StdDraw.setPenColor(StdDraw.BOOK_BLUE);
        int numVertices = mesh.getNumVertices();
        double[] xVertices = new double[numVertices];
        double[] yVertices = new double[numVertices];
        for (int i = 0; i < numVertices; i++) {
            Coordinate transformed = transformCoordinate(mesh.getVertices()[i]);
            xVertices[i] = transformed.getX() + (double) displayWidth / 2;
            yVertices[i] = transformed.getY() + (double) displayHeight / 2;
        }
        StdDraw.filledPolygon(xVertices, yVertices);
    }
    /**
     * Transform a 3D coordinate to a 2D projection on screen.
     * @param position 3D position within world.
     * @return renderable 2D coordinate.
     * */
    public Coordinate transformCoordinate(Coordinate position) {
//        Coordinate cameraPosition = camera.getPosition();
//        double X = position.getX() - cameraPosition.getX();
//        double Y = position.getY() - cameraPosition.getY();
//        double Z = position.getZ() - cameraPosition.getZ();
//        // Theta = (thetaX, thetaY, thetaZ) -> tait-bryan angles
//        Coordinate cameraTilt = camera.getCameraTilt();
//        double thetaX = -Math.toRadians(cameraTilt.getX()); // pitch
//        double thetaY = -Math.toRadians(cameraTilt.getY() - 90); // yaw
//        double thetaZ = Math.toRadians(cameraTilt.getZ()); // roll
//        // I have no idea if this is going to work
//        double dX = Math.cos(thetaY) * (Math.sin(thetaZ) * Y + Math.cos(thetaZ) * X) - Math.sin(thetaY) * Z;
//        double dY = Math.sin(thetaX) * (Math.cos(thetaY) * Z + Math.sin(thetaY) * (Math.sin(thetaZ) * Y + Math.cos(thetaZ) * X)) + Math.cos(thetaX) * (Math.cos(thetaZ) * Y - Math.sin(thetaZ) * X);
//        double dZ = Math.cos(thetaX) * (Math.cos(thetaY) * Z + Math.sin(thetaY) * (Math.sin(thetaZ) * Y + Math.cos(thetaZ) * X)) - Math.sin(thetaX) * (Math.cos(thetaZ) * Y - Math.sin(thetaZ) * X);
//        // E = (eX, eY, eZ) -> position of the display surface plane position relative to the camera pinhole
//        double eX = 0;
//        double eY = 0;
//        double eZ = focalLength;
//        // (bX, bY) -> transformed position on the 2d screen surface
//        double bX = (double) ((eZ / dZ) * dX + eX);
//        double bY = (double) ((eZ / dZ) * dY + eY);
//        return new Coordinate(bX, bY, 0);
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


