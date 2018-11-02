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
     * if empty, or the cheapest solution
     *
     * @return The unique cheapest solution as a List
     */
    @Override
    public List<String> solutions() {
        Map<Integer, EnumSet<PaintFinish>> searchSpace = this.reducedSearchSpace();
        if (searchSpace.isEmpty()) {
            return Collections.emptyList();
        }

        //we have reduced the options. Now, go through each of the paint and retain
        //the cheapest option. An EnumSet being ordered, the cheapest in our case
        //is the first value found
        String solution = IntStream
                .rangeClosed(1, nbPaints)
                .mapToObj(i -> searchSpace.get(i).iterator().next().name()) //take 1st, i.e. G, of each acceptable option
                .collect(Collectors.joining(""));
        LOGGER.debug("solution = {}", solution);
        return Collections.singletonList(solution);
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
        return searchspace; //any value in this should be OK
    }

    boolean assignable(Map<Integer, EnumSet<PaintFinish>> searchspace,
                       Set<PaintReference> paintReferences) {
        if (paintReferences.isEmpty()) {
            //unsatisfiable!
            return false;
        }
        if (paintReferences.size() == 1) {
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
