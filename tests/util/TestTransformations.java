package util;

import edu.princeton.cs.algs4.StdDraw;
import org.junit.Test;
import world.*;
import world.assets.Spacecraft;

import static com.google.common.truth.Truth.assertThat;

public class TestTransformations {
    @Test
    public void testYawTransformations() {
        Spacecraft testCraft;
        Coordinate testPoint;
        Coordinate expectedOutput;
        Coordinate yawRotationOutput;

        // basic yaw transformations
        testCraft = new Spacecraft(0, 0, 0, 0, -90, 0, 10, 10, 10);
        testPoint = new Coordinate(10, 0, 10);
        expectedOutput = new Coordinate(10, 0, -10);
        yawRotationOutput = Coordinate.rotateYaw(testCraft, testPoint);
        assertThat(yawRotationOutput.getX()).isEqualTo(expectedOutput.getX());
        assertThat(yawRotationOutput.getY()).isEqualTo(expectedOutput.getY());
        assertThat(yawRotationOutput.getZ()).isEqualTo(expectedOutput.getZ());

        testCraft = new Spacecraft(0, 0, 0, 0, 180, 0, 10, 10, 10);
        testPoint = new Coordinate(-10, 0, 10);
        expectedOutput = new Coordinate(10, 0, -10);
        yawRotationOutput = Coordinate.rotateYaw(testCraft, testPoint);
        assertThat(yawRotationOutput.getX()).isEqualTo(expectedOutput.getX());
        assertThat(yawRotationOutput.getY()).isEqualTo(expectedOutput.getY());
        assertThat(yawRotationOutput.getZ()).isEqualTo(expectedOutput.getZ());
    }

    @Test
    public void testRelativeDistanceCalculations() {
        RenderableEntity entity = new RenderableEntity(100, 0, 0, 0, 0, 0);
        Camera camera = new Camera(0, 0, 0, 0, 0, 0);

        assertThat(Math.round(100 * camera.distanceToViewPlane(entity)) / 100).isEqualTo(100);

        camera.setDirection(0, 60, 0);
        assertThat(Math.round(100 * camera.distanceToViewPlane(entity)) / 100).isEqualTo(50);

        camera.setDirection(0, 90, 0);
        assertThat(Math.round(100 * camera.distanceToViewPlane(entity)) / 100).isEqualTo(0);
    }

    @Test
    public void testCrossProductCalculations() {
        Coordinate v1;
        Coordinate v2;
        Coordinate crossProduct;
        v1 = new Coordinate(1, 0, 0);
        v2 = new Coordinate(0, 0, 1);
        crossProduct = Coordinate.crossProduct(v2, v1);

        assertThat(crossProduct.getX()).isEqualTo(0);
        assertThat(crossProduct.getY()).isEqualTo(1);
        assertThat(crossProduct.getZ()).isEqualTo(0);

        v1 = new Coordinate(1, 0, 0);
        v2 = new Coordinate(0, 1, 0);
        crossProduct = Coordinate.crossProduct(v1, v2);

        assertThat(crossProduct.getX()).isEqualTo(0);
        assertThat(crossProduct.getY()).isEqualTo(0);
        assertThat(crossProduct.getZ()).isEqualTo(1);
    }

    @Test
    public void testNormalizeCalculations() {
        Coordinate v1;
        Coordinate normalized;
        v1 = new Coordinate(1, 0, 0);
        normalized = Coordinate.normalize(v1);

        assertThat(normalized.getX()).isEqualTo(1);
        assertThat(normalized.getY()).isEqualTo(0);
        assertThat(normalized.getZ()).isEqualTo(0);

        v1 = new Coordinate(1, 1, 1);
        normalized = Coordinate.normalize(v1);

        assertThat(normalized.getX()).isEqualTo(1 / Math.sqrt(3));
        assertThat(normalized.getY()).isEqualTo(1 / Math.sqrt(3));
        assertThat(normalized.getZ()).isEqualTo(1 / Math.sqrt(3));
    }

    @Test
    public void testDotProduct() {
        Coordinate v1;
        Coordinate v2;
        Coordinate crossProduct;

        v1 = new Coordinate(1, 0, 0);
        v2 = new Coordinate(1, 0, 0);
        assertThat(Coordinate.dotProduct(v1, v2)).isEqualTo(1);

        v1 = new Coordinate(1, 0, 0);
        v2 = new Coordinate(0, 1, 0);
        assertThat(Coordinate.dotProduct(v1, v2)).isEqualTo(0);

        v1 = new Coordinate(1, 0, 0);
        v2 = new Coordinate(-1, 0, 0);
        assertThat(Coordinate.dotProduct(v1, v2)).isEqualTo(-1);
    }

    @Test
    public void testNormalVectorCreation() {
        Coordinate v1 = new Coordinate(0, 0, 0);
        Coordinate v2 = new Coordinate(1, 0, 0);
        Coordinate v3 = new Coordinate(0, 1, 0);

        Mesh mesh = new Mesh(new Coordinate[]{v1, v2, v3}, StdDraw.RED);

        Coordinate normal = mesh.getNormalVector();
        assertThat(normal.getX()).isEqualTo(0);
        assertThat(normal.getY()).isEqualTo(0);
        assertThat(normal.getZ()).isEqualTo(1);
    }
}
