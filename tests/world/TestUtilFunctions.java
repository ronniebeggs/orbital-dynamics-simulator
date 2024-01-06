package world;

import org.junit.Test;
import util.Coordinate;
import static com.google.common.truth.Truth.assertThat;

public class TestUtilFunctions {
    @Test
    public void testAngleDistanceCalculations() {
        Coordinate origin = new Coordinate(0, 0);
        Coordinate target = new Coordinate(1, 1);

        assertThat(origin.angleBetween(target)).isEqualTo(Math.PI / 4);
        assertThat(target.angleBetween(origin)).isEqualTo((-3 * Math.PI) / 4);
    }
}
