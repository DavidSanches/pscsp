package me.david.paintshop;

import me.david.paintshop.exceptions.PaintShopInputRuntimeException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;

import static me.david.paintshop.exceptions.PaintShopError.INVALID_CUSTOMER_TASTE;
import static me.david.paintshop.exceptions.PaintShopError.INVALID_INPUT_FILE_NUMBER_OF_PAINTS;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Unit test for {@link SearchPaintShopSolver}
 */
class SearchPaintShopSolverTest {

    @Test
    void testConstructor_invalidTasteGiven_shouldThrownException() {
        try {
            new SearchPaintShopSolver(new LinkedList<>(Arrays.asList("3", "1G2G4M")));

        } catch (Exception e) {
            assertThat(e)
                    .isInstanceOf(PaintShopInputRuntimeException.class)
                    .hasMessage(INVALID_CUSTOMER_TASTE.getDescription() +
                            " - Customer taste '1G2G4M' is not valid. " +
                            "It references an unknown paint: '4' (> 3).")
                    .hasNoCause();
        }
    }

    @Test
    void testFindSolutions_customerWithMoreThatOneMatteTaste_shouldThrowException() {
        try {
            PaintShopSolver solver = new SearchPaintShopSolver(new LinkedList<>(
                    Arrays.asList("3", "1M", "1M2M", "2G")));

            assertThat(solver.solutions())
                    .contains("a", "b", "c");

        } catch (Exception e) {
            assertThat(e)
                    .isInstanceOf(PaintShopInputRuntimeException.class)
                    .hasMessage(INVALID_CUSTOMER_TASTE.getDescription() +
                            " - Customer taste '1M2M' is not valid. More than one Matte finish detected.");
        }
    }


    @Test
    void testPaintShopSolver_nonIntegerFirstLineGiven_shouldThrowAPaintShopInputRuntimeException() {
        try {
            new SearchPaintShopSolver(new LinkedList<>(
                    Arrays.asList("a", "1G2G3M")));

        } catch (Exception e) {
            assertThat(e)
                    .isInstanceOf(PaintShopInputRuntimeException.class)
                    .hasMessage(INVALID_INPUT_FILE_NUMBER_OF_PAINTS.getDescription() +
                            " - First line 'a' is expected to be an integer.")
                    .hasCauseInstanceOf(NumberFormatException.class);
        }
    }

    @Test
    void testPaintShopSolver_invalidColorIndexGiven_shouldThrowAPaintShopInputRuntimeException() {
        try {
            new SearchPaintShopSolver(new LinkedList<>(
                    Arrays.asList("3", "1G4M")));

        } catch (Exception e) {
            assertThat(e)
                    .isInstanceOf(PaintShopInputRuntimeException.class)
                    .hasMessage(INVALID_CUSTOMER_TASTE.getDescription() +
                            " - Customer taste '1G4M' is not valid. " +
                            "It references an unknown paint: '4' (> 3).")
                    .hasNoCause();
        }
    }

    @Test
    void testPaintShopSolver_numberAndInputCustomertastesGiven_shouldExtractNumberAndSortTastes() {

        SearchPaintShopSolver solver = new SearchPaintShopSolver(new LinkedList<>(
                Arrays.asList("3", "1G2G3M", "3M", "2G3M")));

        assertThat(solver.getNbPaints()).isEqualTo(3);
        assertThat(solver.getCustomerTastes())
                .contains("3M", "2G3M", "1G2G3M");
    }


    @Test
    void testSolve_definitionAndCustTastesGiven_shouldFindASetOfSolutionsEachWithCorrectSize() {
        SearchPaintShopSolver solver = new SearchPaintShopSolver(new LinkedList<>(
                Arrays.asList("5", "1M3G5G", "2G3M4G", "5M")));

        assertThat(solver.getNbPaints()).isEqualTo(5);
        assertThat(solver.solutions())
                .contains("GGGGM")
                .allMatch(sol -> sol.length() == solver.getNbPaints()); //There is just one batch for each color, and it's either gloss or matte.
    }

    @Test
    void testSolve_insatisfiableProblemGiven_shouldReturnEmptyList() {
        PaintShopSolver solver = new SearchPaintShopSolver(new LinkedList<>(
                Arrays.asList("3", "1M", "2G", "1G2M")));

        assertThat(solver.solutions())
                .isEmpty();
    }

}