package me.david.paintshop;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link SearchSpaceReducerPaintShopSolver}
 */
class SearchSpaceReducerPaintShopSolverTest {

    @Test
    void testInitialSearchSpace_numberOfPaintsGiven_shouldReturnAllCombinations() {
        SearchSpaceReducerPaintShopSolver solver = new SearchSpaceReducerPaintShopSolver(2,
                Collections.emptyList());

        assertThat(solver.initialSearchSpace())
                .hasSize(2)
                .contains(
                        entry(1, EnumSet.of(PaintFinish.G, PaintFinish.M)),
                        entry(2, EnumSet.of(PaintFinish.G, PaintFinish.M)));
    }


    @Test
    void reducedSearchSpace_unsatisfiableProblemGiven_shouldReturnEmpty() {
        int nbPaints = 3;
        List<CustomerTaste> unsatisfiableCustomerTastes = Stream.of("1G2G", "1G", "1M")
                .map(repr -> new CustomerTaste(nbPaints, repr))
                .collect(Collectors.toList());
        SearchSpaceReducerPaintShopSolver solver = new SearchSpaceReducerPaintShopSolver(nbPaints, unsatisfiableCustomerTastes);

        assertThat(solver.reducedSearchSpace())
                .hasSize(0);

    }

    @Test
    void reducedSearchSpace_unsatisfiableProblemGiven_shouldReturnEmpty_2() {
        int nbPaints = 2;
        List<CustomerTaste> unsortedCustomerTastes = Stream.of("1M", "1G2G", "2M")
                .map(repr -> new CustomerTaste(nbPaints, repr))
                .collect(Collectors.toList());
        SearchSpaceReducerPaintShopSolver solver = new SearchSpaceReducerPaintShopSolver(nbPaints, unsortedCustomerTastes);

        assertThat(solver.reducedSearchSpace())
                .hasSize(0);
    }

    @Test
    void reducedSearchSpace_customerTastesGiven_shouldReturnReducedMapOfOptions_1() {
        int nbPaints = 2;
        List<CustomerTaste> unsortedCustomerTastes = Stream.of("1M", "1M2G", "2M")
                .map(repr -> new CustomerTaste(nbPaints, repr))
                .collect(Collectors.toList());
        SearchSpaceReducerPaintShopSolver solver = new SearchSpaceReducerPaintShopSolver(nbPaints, unsortedCustomerTastes);

        assertThat(solver.reducedSearchSpace())
                .hasSize(nbPaints)
                .contains(
                        entry(1, EnumSet.of(PaintFinish.M)), //G excluded
                        entry(2, EnumSet.of(PaintFinish.M))); //G excluded
    }

    @Test
    void reducedSearchSpace_customerTastesGiven_shouldReturnReducedMapOfOptions_() {
        int nbPaints = 3;
        List<CustomerTaste> unsortedCustomerTastes = Stream.of("1G2G", "3M", "1M")
                .map(repr -> new CustomerTaste(nbPaints, repr))
                .collect(Collectors.toList());
        SearchSpaceReducerPaintShopSolver solver = new SearchSpaceReducerPaintShopSolver(nbPaints, unsortedCustomerTastes);

        assertThat(solver.reducedSearchSpace())
                .hasSize(3)
                .contains(
                        entry(1, EnumSet.of(PaintFinish.M)), //G excluded
                        entry(2, EnumSet.of(PaintFinish.G)), //G cheaper than M
                        entry(3, EnumSet.of(PaintFinish.M))); //G excluded
    }

    @Test
    void reducedSearchSpace_otherCustomerTastesGiven_shouldReturnReducedMapOfOptions() {
        int nbPaints = 3;
        List<CustomerTaste> unsortedCustomerTastes = Stream.of("3M", "1G2G3G", "1M2G")
                .map(repr -> new CustomerTaste(nbPaints, repr))
                .collect(Collectors.toList());
        SearchSpaceReducerPaintShopSolver solver = new SearchSpaceReducerPaintShopSolver(nbPaints, unsortedCustomerTastes);

        assertThat(solver.reducedSearchSpace())
                .hasSize(3)
                .contains(
                        entry(1, EnumSet.of(PaintFinish.G, PaintFinish.M)),
                        entry(2, EnumSet.of(PaintFinish.G, PaintFinish.M)),
                        entry(3, EnumSet.of(PaintFinish.M)));
    }

    @Test
    void testSolutions_unsatisfiableProblemGiven_shouldReturnEmpty() {
        int nbPaints = 3;
        List<CustomerTaste> unsatisfiableCustomerTastes = Stream.of("1G2G", "1G", "1M")
                .map(repr -> new CustomerTaste(nbPaints, repr))
                .collect(Collectors.toList());
        SearchSpaceReducerPaintShopSolver solver = new SearchSpaceReducerPaintShopSolver(nbPaints, unsatisfiableCustomerTastes);

        assertThat(solver.solutions())
                .isEmpty();
    }

    @Test
    void testSolutions_unsatisfiableProblemGiven_shouldReturnEmpty_2() {
        int nbPaints = 3;
        List<CustomerTaste> unsatisfiableCustomerTastes = Stream.of("1M", "2G", "1G2M")
                .map(repr -> new CustomerTaste(nbPaints, repr))
                .collect(Collectors.toList());
        SearchSpaceReducerPaintShopSolver solver = new SearchSpaceReducerPaintShopSolver(nbPaints, unsatisfiableCustomerTastes);

        assertThat(solver.solutions())
                .isEmpty();
    }

    @Test
    void testSolutions_nonEmptyAfterReductionSearchSpace_shouldReturnXXX() {
        int nbPaints = 5;
        List<CustomerTaste> unsatisfiableCustomerTastes = Stream.of(
                "1 M 3 G 5 G", "2 G 3 M 4 G", "5 M")
                .map(repr -> new CustomerTaste(nbPaints, repr))
                .collect(Collectors.toList());
        SearchSpaceReducerPaintShopSolver solver = new SearchSpaceReducerPaintShopSolver(nbPaints, unsatisfiableCustomerTastes);

        assertThat(solver.solutions())
                .hasSize(1)
                .contains("GGGGM");
    }
}