package me.david.paintshop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * This solver tries to reduce the search space before searching a solution.
 * <p>
 * <p>
 * <p>See README.md file for the full definition of the problem.</p>
 * <ul>Main constraints are:
 * <li>There is just one batch for each color, and it's either gloss or matte.</li>
 * <li>For each customer, there is at least one color they like.</li>
 * <li>You make as few mattes as possible (because they are more expensive).</li>
 * </ul>
 */
public class SearchSpaceReducerPaintShopSolver implements PaintShopSolver {

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
    SearchSpaceReducerPaintShopSolver(int nbPaints, List<CustomerTaste> customerTastes) {
        this.nbPaints = nbPaints;

        this.sortedCustomerTastes = customerTastes.stream()
                .sorted(Comparator.comparing(CustomerTaste::count))
                .collect(Collectors.toList());
        LOGGER.debug("sorted cust tastes: {}", this.sortedCustomerTastes);
    }

    /**
     * Retrieves the reduced search space and either return an empty list
     * if empty, or the cheapest solution
     *
     * @return
     */
    @Override
    public List<String> solutions() {
        Map<Integer, Set<PaintFinish>> searchSpace = this.reducedSearchSpace();
        if (searchSpace.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> allCombinations = this.combine(this.nbPaints, searchSpace);
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

    List<String> combine(int index, Map<Integer, Set<PaintFinish>> m) {
        if (index == 1) {
            return m.get(index).stream()
                    .map(paintFinish -> paintFinish.name())
                    .collect(toList());
        } else {
            List<String> recur = combine(index - 1, m);
            Set<PaintFinish> paintFinishes = m.get(index);
            List<String> res = new LinkedList<>();
            for (String pf : recur) {
                for (PaintFinish c : paintFinishes) {
                    res.add(pf + c);
                }
            }
            return res;
        }
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
     * generates the search space.
     * Starts from an intial search apace, a Map: i -> "GM", with i = 1, ..., (nbPaint + 1)
     * It then iterates on the customer tastes and tries to exclude paint finishes
     * from the available options for a paint.
     *
     * @return the reduces search space
     */
    Map<Integer, Set<PaintFinish>> reducedSearchSpace() {
        Map<Integer, Set<PaintFinish>> searchspace = initialSearchSpace();

        for (CustomerTaste taste : this.sortedCustomerTastes) {
            Set<PaintReference> paintReferences = taste.paintReferences();
            if (paintReferences.size() == 1) {
                PaintReference uniquePaintRef = paintReferences.iterator().next();
                Set<PaintFinish> availableFinishOptions = searchspace.get(uniquePaintRef.index());
                if (!availableFinishOptions.contains(uniquePaintRef.finish())) {
                    //unsatisfiable!
                    LOGGER.info("Unsatisfiable - {}", this.sortedCustomerTastes);
                    return Collections.emptyMap();
                }
                availableFinishOptions.remove(uniquePaintRef.finish().opposite());
            } else {//more than 1 paint ref, ensure that at least one of the them is available
                boolean atLeastOneSatisfied = paintReferences.stream()
                        .anyMatch(pf -> searchspace.get(pf.index()).contains(pf.finish()));
                if (!atLeastOneSatisfied) {
                    LOGGER.info("Insatifiable - {}", this.sortedCustomerTastes);
                    return Collections.emptyMap();
                }
            }
        }
        return searchspace; //any value in this should be OK
    }


    /**
     * Generates an initial search space.
     * Paint index is 1-based
     *
     * @return the initial search space (Paint index -> "GM")
     */
    Map<Integer, Set<PaintFinish>> initialSearchSpace() {
        Map<Integer, Set<PaintFinish>> searchspace = new HashMap();
        for (int index = 1; index < (this.nbPaints + 1); index++) {
            searchspace.put(index, EnumSet.of(PaintFinish.G, PaintFinish.M));
        }
        return searchspace;
    }

}
