package me.david.paintshop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;

/**
 * This solver tries to reduce the search space before searching a solution.
 * <p>See README.md file for the full definition of the problem.</p>
 * <p>Main constraints are:</p>
 * <ul>
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
     * if empty (i.e. unsatisfiable), or run a search with CustomerTastes on the remaining options
     * and finally return the cheapest option of any
     *
     * @return The unique cheapest solution as a List
     */
    @Override
    public List<String> solutions() {
        Map<Integer, EnumSet<PaintFinish>> searchSpace = this.reducedSearchSpace();
        if (searchSpace.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> allCombinations = combine(nbPaints, searchSpace);
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


    private List<String> combine(int index, Map<Integer, EnumSet<PaintFinish>> csp) {
        if (index == 0) {
            return Collections.singletonList("");
        } else {
            List<String> recur = combine(index - 1, csp);
            List<String> current = csp.get(index)
                    .stream()
                    .map(PaintFinish::name)
                    .collect(Collectors.toList());
            List<String> res = new LinkedList<>();
            for (String c : current) {
                for (String pf : recur) {
                    res.add(pf + c);
                }
            }
            return res;
        }
    }


    /**
     * generates the search space.
     * Starts from an intial search apace, a Map: i -> "GM", with i = 1, ..., (nbPaint + 1)
     * It then iterates on the customer tastes and tries to exclude paint finishes
     * from the available options for a paint.
     *
     * @return the reduces search space
     */
    Map<Integer, EnumSet<PaintFinish>> reducedSearchSpace() {
        Map<Integer, EnumSet<PaintFinish>> searchspace = initialSearchSpace();

        for (CustomerTaste taste : this.sortedCustomerTastes) {
            Set<PaintReference> paintReferences = taste.paintReferences();
            if (!assignable(searchspace, paintReferences)) {
                //unsatisfiable!
                LOGGER.info("Unsatisfiable - {}", this.sortedCustomerTastes);
                return Collections.emptyMap();
            }
        }
        return searchspace; //does not contain invalid paint reference
    }

    /**
     *
     * @param searchspace
     * @param paintReferences consituting the tas of the customer (e.g. 1M, 3G, ...)
     * @return
     */
    boolean assignable(Map<Integer, EnumSet<PaintFinish>> searchspace,
                       Set<PaintReference> paintReferences) {
        if (paintReferences.isEmpty()) {
            //unsatisfiable set of paint reference
            return false;
        }
        if (paintReferences.size() == 1) {//customer only likes one Paint in one finish, check satisfiability + prune the other finish for that paint
            PaintReference uniquePaintRef = paintReferences.iterator().next();
            Set<PaintFinish> availableFinishOptions = searchspace.get(uniquePaintRef.index());
            if (!availableFinishOptions.contains(uniquePaintRef.finish())) {
                //unsatisfiable!
                return false;
            }
            availableFinishOptions.remove(uniquePaintRef.finish().opposite());
            return true;
        } else {
            //prune unsatisfiable taste
            Set<PaintReference> remainingSatisfiableOptions = paintReferences.stream()
                    .filter(ref -> searchspace.get(ref.index()).contains(ref.finish()))
                    .collect(toSet());
            LOGGER.debug("remainingSatisfiableOptions = {}", remainingSatisfiableOptions);
            if (remainingSatisfiableOptions.equals(paintReferences)) {
                return true;
            } else {
                return assignable(searchspace, remainingSatisfiableOptions);
            }
        }
    }

    /**
     * Generates an initial search space.
     * Paint index is 1-based
     *
     * @return the initial search space (Paint index -> "GM")
     */
    Map<Integer, EnumSet<PaintFinish>> initialSearchSpace() {
        Map<Integer, EnumSet<PaintFinish>> searchspace = new HashMap();
        for (int index = 1; index < (this.nbPaints + 1); index++) {
            searchspace.put(index, EnumSet.of(PaintFinish.G, PaintFinish.M));
        }
        return searchspace;
    }

}
