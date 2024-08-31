package engine;

import Jama.Matrix;
import edu.princeton.cs.algs4.StdDraw;
import util.Coordinate;
import util.Mesh;
import util.Transformations;
import world.*;

import java.awt.Color;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

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
    private Camera camera;
    private double focalLength;
    private List<Satellite> orderedTargetList;
    private Entity[] lightSources;
    private Set<Entity> lightEmitters;
    private Satellite targetSatellite;

    public void initialize(int width, int height, double scaleFactor, Camera camera, Satellite targetSatellite, List<Satellite> orderedChildren, Entity[] lightSources, Set<Entity> lightEmitters) {
        this.displayWidth = width;
        this.displayHeight = height;
        this.scaleFactor = scaleFactor;
        this.camera = camera;
        int verticalViewAngle = 60;
        this.focalLength = displayHeight / (2 * Math.tan(Math.toRadians(verticalViewAngle)));
        this.targetSatellite = targetSatellite;
        this.orderedTargetList = orderedChildren;
        this.lightSources = lightSources;
        this.lightEmitters = lightEmitters;

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
            // render a satellite marker with constant size regardless of zoom
            if (camera.distanceToViewPlane(satellite.getPosition()) >= 100) {
                if (satellite.equals(targetSatellite)) {
                    renderSatelliteMarker(satellite.getPosition(), StdDraw.GREEN);
                } else {
                    renderSatelliteMarker(satellite.getPosition(), StdDraw.PRINCETON_ORANGE);
                }
            }
        }
        StdDraw.show();
    }
    /**
     * Method for rendering satellite objects.
     * @param satellite specified satellite instance.
     * */
    public void renderSatellite(Satellite satellite) {
        // map each mesh to its distance relative to the camera, and place within priority queue.
        PriorityQueue<MeshRankNode> meshRank = new PriorityQueue<>();
        for (Mesh mesh : satellite.getMeshes()) {
            Coordinate meshPosition = mesh.averageWorldPosition();
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

    public void renderSatelliteMarker(Coordinate satelliteCenter, Color color) {
        StdDraw.setPenColor(color);
        Coordinate transformed = transformCoordinate(satelliteCenter);
        double displayX = transformed.getX() + (double) displayWidth / 2;
        double displayY = transformed.getY() + (double) displayHeight / 2;

        StdDraw.filledPolygon(
                new double[]{displayX, displayX - 5, displayX + 5},
                new double[]{displayY, displayY + 10, displayY + 10}
        );
    }

    /**
     * Shade the mesh using the `lightSource`s in the simulation.
     * @param mesh target mesh to apply the shader too.
     * @return adjusted shader color.
     * */
    public Color shadeMesh(Mesh mesh) {
        double strongestFacingRatio = 0.1;
        // iterate through lightSources and find the brightest light
        for (int lightIndex = 0; lightIndex < lightSources.length; lightIndex++) {
            Entity light = lightSources[lightIndex];
            // light sources shouldn't light themselves up
            if (light.equals(mesh.getParent())) {
                continue;
            }

            Coordinate lightPosition = light.getPosition();
            Coordinate meshPosition = mesh.averagePosition();
            Coordinate meshParentPosition = mesh.getParent().getPosition();
            Coordinate lightVector = Transformations.normalize(new Coordinate(
                    lightPosition.getX() - (meshParentPosition.getX() + meshPosition.getX()),
                    lightPosition.getY() - (meshParentPosition.getY() + meshPosition.getY()),
                    lightPosition.getZ() - (meshParentPosition.getZ() + meshPosition.getZ())
            ));

            double facingRatio = Transformations.dotProduct(lightVector, mesh.getNormalVector());
            if (facingRatio > strongestFacingRatio) {
                strongestFacingRatio = facingRatio;
            }
        }
        // shade the mesh according to the brightest light source
        double brightnessProportion = strongestFacingRatio * 1;
        float[] colorComponents = new float[3];
        mesh.getColor().getColorComponents(colorComponents);
        return new Color(
                (int) (colorComponents[0] * brightnessProportion * 255),
                (int) (colorComponents[1] * brightnessProportion * 255),
                (int) (colorComponents[2] * brightnessProportion * 255)
        );
    }

    /**
     * Draw and fill the mesh using StdDraw library.
     * @param mesh mesh to be renxdered.
     * */
    public void renderMesh(Mesh mesh) {
        RenderableEntity meshParent = mesh.getParent();
        // light emitting entities won't have shadows
        if (lightEmitters.contains(meshParent)) {
            StdDraw.setPenColor(mesh.getColor());
        } else {
            Color adjustedColor = shadeMesh(mesh);
            StdDraw.setPenColor(adjustedColor);
        }
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
        double pitch = Math.toRadians(cameraDirection.getX());
        double yaw = Math.toRadians(cameraDirection.getY());

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
        Coordinate meshPosition = mesh.averagePosition();
        Coordinate meshParentPosition = mesh.getParent().getPosition();
        return camera.distanceToViewPlane(
                new Coordinate(
                        meshParentPosition.getX() + meshPosition.getX(),
                        meshParentPosition.getY() + meshPosition.getY(),
                        meshParentPosition.getZ() + meshPosition.getZ()
                )
        ) >= frontClip;
    }
}


