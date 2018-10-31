package me.david.paintshop;

import me.david.paintshop.PaintFinish;
import me.david.paintshop.PaintReference;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

class PaintReferenceTest {

    @Test
    void testConstructors() {
        PaintReference ref1 = new PaintReference("1", "M");
        PaintReference ref2 = new PaintReference(1, PaintFinish.M);

        assertThat(ref1).isEqualTo(ref2);
    }

    @Test
    void testGetters() {
        PaintReference ref1 = new PaintReference("1", "M");

        assertThat(ref1.index()).isEqualTo(1);
        assertThat(ref1.finish()).isEqualTo(PaintFinish.M);
    }
}