package me.david.paintshop;

import me.david.paintshop.PaintBatches;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Unit test for {@link PaintBatches}
 */
class PaintBatchesTest {

    @Test
    void testCost_oneGlossOneMatteGiven_shouldGiveGlossCheaperThanMatte() {
        PaintBatches oneColorGloss = new PaintBatches("G");
        PaintBatches oneColorMatte = new PaintBatches("M");

        assertThat(oneColorGloss.cost())
                .isLessThan(oneColorMatte.cost());
        assertThat(oneColorGloss.cost())
                .isLessThan(oneColorMatte.cost());
    }

    @Test
    void testCost_mixOfGlossAndMatteGiven_shouldGiveGlossCheaperThanMatte() {

        assertThat(new PaintBatches("GGG").cost())
                .isLessThan(new PaintBatches("GGM").cost());

        assertThat(new PaintBatches("GMM").cost())
                .isEqualTo(new PaintBatches("MMG").cost());
    }


    @Test
    void testToString_shouldJustReturnTheInputFinishesRepresentation() {
        String repr = "GGGMM";
        String expected = "G G G M M";

        assertThat(new PaintBatches(repr).toString())
                .isEqualTo(expected);
    }


    @Test
    void testSolutionRepresentation_spacelessRepresentationGiven_shouldReturnWithSpacesAsExpectedFormat() throws FileNotFoundException {
        assertThat(new PaintBatches("GGGGM").toString())
                .isEqualTo("G G G G M");
    }

}