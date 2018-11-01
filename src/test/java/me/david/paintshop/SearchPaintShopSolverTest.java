package me.david.paintshop;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Unit test for {@link SearchPaintShopSolver}
 */
class SearchPaintShopSolverTest {

    @Test
    void testPaintShopSolver_unsortedCustomerTastesGiven_shouldSortTastes() {
        int nbPaints = 3;
        List<CustomerTaste> unsortedCustomerTastes = Stream.of("1G2G3M", "3M", "2G3M")
                .map(repr -> new CustomerTaste(nbPaints, repr))
                .collect(Collectors.toList());
        SearchPaintShopSolver solver = new SearchPaintShopSolver(3, unsortedCustomerTastes);

        assertThat(solver.sortedCustomerTastes())
                .contains("3M", "2G3M", "1G2G3M");
    }


    @Test
    void testSolve_definitionAndCustTastesGiven_shouldFindASetOfSolutionsEachWithCorrectSize() {
        int nbPaints = 5;
        List<CustomerTaste> unsortedCustomerTastes = Stream.of("1M3G5G", "2G3M4G", "5M")
                .map(repr -> new CustomerTaste(nbPaints, repr))
                .collect(Collectors.toList());
        SearchPaintShopSolver solver = new SearchPaintShopSolver(nbPaints, unsortedCustomerTastes);

        assertThat(solver.solutions())
                .contains("GGGGM")
                .allMatch(sol -> sol.length() == nbPaints); //There is just one batch for each color, and it's either gloss or matte.
    }

    @Test
    void testSolve_definitionAndCustTastesGiven_shouldReturnOneBatchForEachColor() {
        int nbPaints = 3;
        List<CustomerTaste> unsortedCustomerTastes = Stream.of("1M", "3M") //nothing about paint 2
                .map(repr -> new CustomerTaste(nbPaints, repr))
                .collect(Collectors.toList());
        SearchPaintShopSolver solver = new SearchPaintShopSolver(nbPaints, unsortedCustomerTastes);

        assertThat(solver.solutions())
                .contains("MGM", "MMM")
                .allMatch(sol -> sol.length() == nbPaints); //There is just one batch for each color, and it's either gloss or matte.
    }

    @Test
    void testSolve_insatisfiableProblemGiven_shouldReturnEmptyList() {
        int nbPaints = 3;
        List<CustomerTaste> unsortedCustomerTastes = Stream.of("1M", "2G", "1G2M")
                .map(repr -> new CustomerTaste(nbPaints, repr))
                .collect(Collectors.toList());
        SearchPaintShopSolver solver = new SearchPaintShopSolver(nbPaints, unsortedCustomerTastes);

        assertThat(solver.solutions())
                .isEmpty();
    }

}