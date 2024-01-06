package world;

import org.junit.Test;
import static com.google.common.truth.Truth.assertThat;

public class TestWorldInitialization {
    public int index;
    public int[] array;
    public int changeIndex(int change) {
        index = ((index + change) + array.length) % array.length;
        return index;
    }

    @Test
    public void testChangeIndex() {
        array = new int[4];
        index = 0;
        assertThat(changeIndex(1)).isEqualTo(1);
        assertThat(changeIndex(1)).isEqualTo(2);
        assertThat(changeIndex(-2)).isEqualTo(0);
        assertThat(changeIndex(-1)).isEqualTo(3);
        assertThat(changeIndex(1)).isEqualTo(0);
    }
}