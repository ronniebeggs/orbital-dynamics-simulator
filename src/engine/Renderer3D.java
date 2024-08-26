package engine;

import Jama.Matrix;
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
    private double focalLength;
    private Satellite simulationCenter;
    private List<Satellite> orderedTargetList;
    private Satellite targetSatellite;

    public void initialize(int width, int height, double scaleFactor, Camera camera, Satellite simulationCenter, List<Satellite> orderedChildren) {
        this.displayWidth = width;
        this.displayHeight = height;
        this.scaleFactor = scaleFactor;
        this.camera = camera;
        int verticalViewAngle = 60;
        this.focalLength = displayHeight / (2 * Math.tan(Math.toRadians(verticalViewAngle)));

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
            renderSatellite(satellite);
//            if (satellite instanceof Planet planet) {
//                renderPlanet(planet);
//            }
//            else if (satellite instanceof Spacecraft spacecraft) {
//                renderSpacecraft(spacecraft);
//            }
////            // render a satellite marker with constant size regardless of zoom
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
            Mesh currentMesh = meshRank.remove().mesh;
            if (shouldRenderMesh(currentMesh, 1000)) {
                renderMesh(currentMesh);
            }
        }
    }

    public void renderSatellite(Satellite satellite) {
        // map each mesh to its distance relative to the camera, and place within priority queue.
        PriorityQueue<MeshRankNode> meshRank = new PriorityQueue<>();
        for (Mesh mesh : satellite.getMeshes()) {
            Coordinate meshPosition = mesh.averagePosition();
            Coordinate cameraPosition = camera.getPosition();
            double distanceToCamera = cameraPosition.distance3D(meshPosition);
            meshRank.add(new MeshRankNode(mesh, distanceToCamera));
        }
        // render each mesh in decreasing order relative to the camera to mitigate rendering overlap.
        while (!meshRank.isEmpty()) {
            Mesh currentMesh = meshRank.remove().mesh;
            if (shouldRenderMesh(currentMesh, 1000)) {
                renderMesh(currentMesh);
            }
        }
    }

    /**
     * Draw and fill the mesh using StdDraw library.
     * @param mesh mesh to be rendered.
     * */
    public void renderMesh(Mesh mesh) {
//        Color adjustedColor = shadeMesh(mesh);
        StdDraw.setPenColor(mesh.getColor());
        int numVertices = mesh.getNumVertices();
        double[] xVertices = new double[numVertices];
        double[] yVertices = new double[numVertices];
        for (int i = 0; i < numVertices; i++) {
            Coordinate meshVertex = mesh.getVertices()[i];
            Coordinate meshParentPosition = mesh.getParent().getPosition();
            Coordinate adjustedVertex = new Coordinate(
                    meshParentPosition.getX() + meshVertex.getX(),
                    meshParentPosition.getY() + meshVertex.getY(),
                    meshParentPosition.getZ() + meshVertex.getZ()
            );
            Coordinate transformed = transformCoordinate(adjustedVertex);
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
        Coordinate cameraPosition = camera.getPosition();

        double X = position.getX() - cameraPosition.getX();
        double Y = position.getY() - cameraPosition.getY();
        double Z = position.getZ() - cameraPosition.getZ();
        // Theta = (thetaX, thetaY, thetaZ) -> tait-bryan angles
        Coordinate cameraDirection = camera.getDirection();
        double pitch = 0;
        double yaw = camera.getAbsoluteDirection();
//        double pitch = Math.toRadians(cameraDirection.getX()); // pitch
//        double yaw = Math.toRadians(cameraDirection.getY()); // yaw

        Matrix inversePitchRotation = new Matrix(new double[][]{
                new double[]{Math.cos(pitch), 0, -Math.sin(pitch)},
                new double[]{0, 1, 0},
                new double[]{Math.sin(pitch), 0, Math.cos(pitch)}
        });
        Matrix inverseYawRotation = new Matrix(new double[][]{
                new double[]{Math.cos(yaw), Math.sin(yaw), 0},
                new double[]{-Math.sin(yaw), Math.cos(yaw), 0},
                new double[]{0, 0, 1}
        });
        Matrix XYZMatrix =  new Matrix(new double[][]{
                new double[]{X},
                new double[]{Y},
                new double[]{Z}
        });
        Matrix result = inversePitchRotation.times(inverseYawRotation.times(XYZMatrix));
        // D = (dX, dY, dZ) -> position of the entity relative to the rotated camera (left-hand coordinate system)
        double dX = result.get(0, 0);
        double dY = result.get(1, 0);
        double dZ = result.get(2, 0);
        // E = (eX, eY, eZ) -> position of the display surface plane position relative to the camera pinhole
        double eX = 0;
        double eY = 0;
        double eZ = focalLength;
        // (bX, bY) -> transformed position on the 2d screen surface
        double bX = (double) ((eZ / dX) * dY + eX);
        double bY = (double) ((eZ / dX) * dZ + eY);
        return new Coordinate(bX, bY, 0);


//        Coordinate cameraPosition = camera.getPosition();
//        double X = realToDisplayUnits(position.getX() - cameraPosition.getX());
//        double Y = realToDisplayUnits(position.getY() - cameraPosition.getY());
//        double Z = realToDisplayUnits(position.getZ() - cameraPosition.getZ());
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
     * Scale simulation distances to display distances.
     * @param realPosition simulation distance to be scaled (km).
     * @return resulting distance relative to the display (display pixels).
     * */
    private double realToDisplayUnits(double realPosition) {
        return Math.round(realPosition / scaleFactor);
    }

    public boolean shouldRenderMesh(Mesh mesh, double frontClip) {
        return camera.distanceToViewPlane(mesh) >= frontClip;
    }
}


