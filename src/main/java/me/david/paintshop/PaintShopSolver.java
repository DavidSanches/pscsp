package me.david.paintshop;

import me.david.paintshop.exceptions.PaintShopInputRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static me.david.paintshop.exceptions.PaintShopError.INVALID_INPUT_FILE_NUMBER_OF_PAINTS;

/**
 * This is the main class used to solve the Problem.
 * <p>See README.mf file for the full definition.</p>
 * <ul>Main constraints are:
 * <li>There is just one batch for each color, and it's either gloss or matte.</li>
 * <li>For each customer, there is at least one color they like.</li>
 * <li>You make as few mattes as possible (because they are more expensive).</li>
 * </ul>
 */
public class PaintShopSolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaintShopSolver.class);

    private final List<CustomerTaste> customerTastes;
    private final int nbPaints;


    public PaintShopSolver(List<String> problemDefinition) {
        Deque<String> definition = problemDefinition
                .stream()
                .map(line -> line.replace(" ", ""))
                .collect(Collectors.toCollection(LinkedList::new));

        String firstLine = definition.removeFirst();
        try {
            this.nbPaints = Integer.valueOf(firstLine);
            LOGGER.debug("nbPaints: {}", nbPaints);

        } catch (NumberFormatException e) {
            throw new PaintShopInputRuntimeException(
                    INVALID_INPUT_FILE_NUMBER_OF_PAINTS,
                    String.format("First line '%s' is expected to be an integer.", firstLine), e);
        }
        this.customerTastes = definition.stream()
                .map(repr -> new CustomerTaste(nbPaints, repr))
                .sorted(Comparator.comparing(CustomerTaste::count))
                .collect(Collectors.toList());
        LOGGER.debug("cust tastes: {}", this.customerTastes);
    }


    /**
     * Return a list of solutions to the problem.
     * <br/>Each solution in a string representing the paints, e.g. 'GGG', 'GGM'.
     * <br/>The index of each 'G/M' character is the 1-base index of the paint
     *
     * @return the list of solutions
     */
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
     * return true if customer tastes are satified by a paint combination
     * represented as a String
     *
     * @param combination string representation of the {@link #nbPaints} (e.g. GGGGM, GMGMG,...)
     * @return true if all satisfied, else false
     */
    private boolean allCustomerTastesAreSatisfiedBy(String combination) {
        return this.customerTastes.stream()
                .allMatch(ct -> ct.likes(combination));
    }

    /**
     * Return all combinations of paints for the number of paints given.
     * <br/>E.g.: [GGGGG, GGGGM, GGGMG, GGGMM, GGMGG,...]
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

    public List<String> getCustomerTastes() {
        return customerTastes.stream()
                .map(CustomerTaste::toString)
                .collect(Collectors.toList());
    }

    public int getNbPaints() {
        return nbPaints;
    }
}
