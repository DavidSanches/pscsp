package me.david.paintshop;

import me.david.paintshop.PaintFinish;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Unit test for {@link PaintFinish}
 */
class PaintFinishTest {

    @Test
    void testPaintFinishCost_shouldEnsureGlossIsCheaperThanMatte() {
        assertThat(PaintFinish.G.cost())
                .isLessThan(PaintFinish.M.cost());
    }

    @Test
    void testMatch_characterGorMgiven_shouldmatchRelatedFinish() {
        assertThat(PaintFinish.G.matches('G'));
        assertThat(PaintFinish.M.matches('M'));
    }

    @Test
    void testMatch_characterUnknownGiven_shouldNotMatchAFinish() {
        assertThat(PaintFinish.G.matches('A'));
        assertThat(PaintFinish.M.matches('X'));
    }

}