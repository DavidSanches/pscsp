package me.david.paintshop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is a 'brute-force' search implementation of a solver: it generate all
 * combinations of paint and iterate on each of them with the customer tastes.
 * It sorts the list customer tastes by 'count' to try to exclude a paint
 * combination as soon as possible.
 *
 * <p>See README.md file for the full definition of the problem.</p>
 * <p>Main constraints are:</p>
 * <ul>
 * <li>There is just one batch for each color, and it's either gloss or matte.</li>
 * <li>For each customer, there is at least one color they like.</li>
 * <li>You make as few mattes as possible (because they are more expensive).</li>
 * </ul>
 */
public class SearchPaintShopSolver implements PaintShopSolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchPaintShopSolver.class);

    private final int nbPaints;
    private final List<CustomerTaste> sortedCustomerTastes;

    /**
     * Constructor.
     * Takes the number of paints, and then the unsorted customer tastes.
     * It assumes the customer tastes are unsorted and sort them by {@link CustomerTaste#count()}.
     *
     * @param nbPaints       the number of paints as an int
     * @param customerTastes the customer tastes
     */
    SearchPaintShopSolver(int nbPaints, List<CustomerTaste> customerTastes) {
        this.nbPaints = nbPaints;

        this.sortedCustomerTastes = customerTastes.stream()
                .sorted(Comparator.comparing(CustomerTaste::count))
                .collect(Collectors.toList());
        LOGGER.debug("sorted cust tastes: {}", this.sortedCustomerTastes);
    }

    /**
     * @return the sorted list of customer tastes as {@link String}
     */
    List<CustomerTaste> sortedCustomerTastes() {
        return sortedCustomerTastes;
    }


    /**
     * Return a list of solutions to the problem.
     * <br>Each solution in a string representing the paints, e.g. 'GGG', 'GGM'.
     * <br>The index of each 'G/M' character is the 1-base index of the paint
     *
     * @return the list of solutions
     */
    @Override
    public List<String> solutions() {
        List<String> allCombinations = this.combinePaints(this.nbPaints);
        List<String> solutions = new LinkedList<>();
        for (String combination : allCombinations) {
            if (allCustomerTastesAreSatisfiedBy(combination)) {
                solutions.add(combination);
            }
        }
        LOGGER.debug("allCombinations - length: {}", allCombinations.size());
        LOGGER.debug("solutions - length: {}", solutions.size());
        return solutions;
    }

    /**
     * return true if customer tastes are satisfied by a paint combination
     * represented as a String.
     * <p>Notice that the <code>allMatch</code> implementation returns early when a
     * falsy expression is met.</p>
     *
     * @param combination string representation of the {@link #nbPaints} (e.g. GGGGM, GMGMG,...)
     * @return true if all satisfied, else false
     */
    private boolean allCustomerTastesAreSatisfiedBy(String combination) {
        return this.sortedCustomerTastes.stream()
                .allMatch(ct -> ct.likes(combination));
    }

    /**
     * Return all combinations of paints for the number of paints given.
     * <br>E.g.: [GGGGG, GGGGM, GGGMG, GGGMM, GGMGG,...]
     *
     * @param nb number (count) of paints
     * @return a list of paint reference as a string
     */
    private List<String> combinePaints(int nb) {
        if (nb == 1) {
            return Arrays.asList("G", "M");
        } else {
            List<String> recur = combinePaints(nb - 1);
            List<String> current = Arrays.asList("G", "M");
            List<String> res = new LinkedList<>();
            for (String c : current) {
                for (String pf : recur) {
                    res.add(c + pf);
                }
            }
            return res;
        }
    }

}
