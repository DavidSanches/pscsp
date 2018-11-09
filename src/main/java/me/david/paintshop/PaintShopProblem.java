package me.david.paintshop;

import me.david.paintshop.exceptions.PaintShopError;
import me.david.paintshop.exceptions.PaintShopInputRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static me.david.paintshop.exceptions.PaintShopError.INVALID_INPUT_FILE_NUMBER_OF_PAINTS;


/**
 * Class used for the me.david.paintshop.PaintShop CSP to solve.
 * It accepts an paint order/problem filename as a parameter
 * Call method {@link #solution()} ()} to retrieve the (cheapest) solution
 * to the problem or the default {@link #NO_SOLUTION_FOUND} message.
 * <p>Internally, it uses a solver.</p>
 */
public class PaintShopProblem {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaintShopProblem.class);

    private static final String NO_SOLUTION_FOUND = "No solution exists";

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;


    private final File input;

    PaintShopProblem(String filename) throws FileNotFoundException {
        this.input = new File(filename);
        if (!input.exists() || !input.isFile()) {
            throw new FileNotFoundException("Not found given Filename '" + filename + "'.");
        }
    }

    /**
     * @return the (cheapest) solution to the paint shop problem as a {@link String}
     * representation of the <code>n</code> paints
     */
    public String solution() {
        return this.cheapestSolution();
    }

    /**
     * Get the list of solution from a {@link PaintShopSolver} and return the one of the cheapest found. Cost is
     * based on {@link PaintBatches#cost}.
     * <p>Uses an instance of {@link SearchPaintShopSolver}</p>
     *
     * @return the cheapest solution to the paint shop problem as a {@link String}
     * representation of the <code>n</code> paints
     */
    private String cheapestSolution() {
        long startTime = System.nanoTime();

        Deque<String> problemDefinition = this.problemDefinition();
        int nbPaints = this.parseNbPaints(problemDefinition.removeFirst());
        LOGGER.debug("nbPaints: {}", nbPaints);

        List<CustomerTaste> unsortedCustomerTastes = problemDefinition.stream()
                .map(repr -> new CustomerTaste(nbPaints, repr))
                .collect(Collectors.toList());

//        PaintShopSolver solver = new SearchPaintShopSolver(nbPaints, unsortedCustomerTastes);
//        PaintShopSolver solver = new SearchSpaceReducerPaintShopSolver(nbPaints, unsortedCustomerTastes);
        PaintShopSolver solver = new SatSolver(nbPaints, unsortedCustomerTastes);
        List<String> solutions = solver.solutions();
        final String solution = this.cheapestSolution(solutions)
                .map(PaintBatches::toString)
                .orElse(NO_SOLUTION_FOUND);
        long endTime = System.nanoTime();
        long durationNs = endTime - startTime;//ns = nanoseconds (/1_000_000 to get ms)
        LOGGER.info("{\"elapsed_ns\": {},\"nbpaints\":{},\"solution\":\"{}\"}",
                durationNs, nbPaints, solution);

        return solution;
    }


    /**
     * @return the problem definition from the given file
     */
    Deque<String> problemDefinition() {
        try (BufferedReader reader = Files.newBufferedReader(this.input.toPath(), DEFAULT_CHARSET)) {
            Deque<String> result = new LinkedList<>();
            for (; ; ) {
                String line = reader.readLine();
                if (line == null)
                    break;
                result.add(line);
            }
            return result;

        } catch (IOException e) {
            throw new PaintShopInputRuntimeException(
                    PaintShopError.INVALID_INPUT_FILE,
                    "Check that you can read the input file as " + DEFAULT_CHARSET, e);
        }
    }

    /**
     * parse the number of paint from a String to an int
     *
     * @param number string value of the number of paints
     * @return the int value
     */
    int parseNbPaints(String number) {
        try {
            return Integer.valueOf(number);

        } catch (NumberFormatException e) {
            throw new PaintShopInputRuntimeException(
                    INVALID_INPUT_FILE_NUMBER_OF_PAINTS,
                    String.format("First line '%s' is expected to be an integer.", number), e);
        }
    }

    /**
     * Returns the cheapest of the given solutions. It instantiate each String solution into a {@link PaintBatches}
     * instance and uses the {@link PaintBatches#cost()} method for sorting.
     * The cheapest option is considered the first, after the sorting.
     *
     * @param solutions list of String solutions
     * @return an {@link Optional} solution as a {@link PaintBatches} instance
     */
    Optional<PaintBatches> cheapestSolution(List<String> solutions) {
        return solutions.stream()
                .filter(Objects::nonNull)
                .map(PaintBatches::new)
                .sorted(Comparator.comparingInt(PaintBatches::cost)) //sort by cost "You make as few mattes as possible (because they are more expensive)"
                .findFirst();
    }
}
