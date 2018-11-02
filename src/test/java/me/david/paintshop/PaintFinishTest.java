package me.david.paintshop;

import me.david.paintshop.PaintFinish;
import org.junit.jupiter.api.Test;

import static me.david.paintshop.PaintFinish.G;
import static me.david.paintshop.PaintFinish.M;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Unit test for {@link PaintFinish}
 */
class PaintFinishTest {

    @Test
    void testPaintFinishCost_shouldEnsureGlossIsCheaperThanMatte() {
        assertThat(G.cost())
                .isLessThan(M.cost());
    }

    @Test
    void testMatch_characterGorMgiven_shouldmatchRelatedFinish() {
        assertThat(G.matches('G'));
        assertThat(M.matches('M'));
    }

    @Test
    void testMatch_characterUnknownGiven_shouldNotMatchAFinish() {
        assertThat(G.matches('A'));
        assertThat(M.matches('X'));
    }

    @Test
    void testOpposite_GlossGiven_shouldReturnMatte() {
        assertThat(G.opposite()).isEqualTo(M);
    }

    @Test
    void testOpposite_MatteGiven_shouldReturnGloss() {
        assertThat(PaintFinish.M.opposite()).isEqualTo(G);
    }
}