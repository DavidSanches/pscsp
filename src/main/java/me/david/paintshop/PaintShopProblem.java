package me.david.paintshop;

import me.david.paintshop.exceptions.PaintShopError;
import me.david.paintshop.exceptions.PaintShopInputRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Class used for the me.david.paintshop.PaintShop CSP to solve.
 * It accepts an paint order/problem filename as a parameter
 * Call method {@link #solution()} ()} to retrieve the (cheapest) solution
 * to the problem or the default {@link #NO_SOLUTION_FOUND} message.
 * <p>
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
            throw new FileNotFoundException("Given Filename is incorrect");
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
        SearchPaintShopSolver solver = new SearchPaintShopSolver(
                this.problemDefinition());
        List<String> solutions = solver.solutions();

        Optional<PaintBatches> cheapestSolution =
                solutions.stream()
                        .filter(Objects::nonNull)
                        .map(PaintBatches::new)
                        .sorted(Comparator.comparingInt(PaintBatches::cost)) //sort by cost "You make as few mattes as possible (because they are more expensive)"
                        .findFirst();

        final String solution = cheapestSolution
                .map(PaintBatches::toString)
                .orElse(NO_SOLUTION_FOUND);
        long endTime = System.nanoTime();
        long durationNs = endTime - startTime;//ns = nanoseconds (/1_000_000 to get ms)
        LOGGER.info("{\"elapsed_ns\": {},\"nbpaints\":{},\"solution\":\"{}\"}",
                durationNs, solver.getNbPaints(), solution);

        return solution;
    }


    /**
     * @return the problem definition from the given file
     */
    Deque<String> problemDefinition() {
        try (InputStream resource = new FileInputStream(this.input)) {
            List<String> lines =
                    new BufferedReader(new InputStreamReader(resource,
                            StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
            return new ArrayDeque<>(lines);
        } catch (Exception e) {
            throw new PaintShopInputRuntimeException(
                    PaintShopError.INVALID_INPUT_FILE,
                    "Check that you can read the input file as " + DEFAULT_CHARSET, e);
        }
    }

}
