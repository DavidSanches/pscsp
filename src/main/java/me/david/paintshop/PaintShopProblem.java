package me.david.paintshop;

import me.david.paintshop.exceptions.PaintShopError;
import me.david.paintshop.exceptions.PaintShopInputRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


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

    public PaintShopProblem(String filename) throws FileNotFoundException {
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
     * @return the cheapest solution to the paint shop problem as a {@link String}
     * representation of the <code>n</code> paints
     */
    private String cheapestSolution() {
        long startTime = System.nanoTime();
        PaintShopSolver solver = new PaintShopSolver(
                this.problemDefinition());
        List<String> solutions = solver.solutions();

        Optional<PaintBatches> cheapestSolution =
                solutions.stream()
                        .filter(Objects::nonNull)
                        .map(PaintBatches::new)
                        .sorted(Comparator.comparingInt(PaintBatches::cost)) //sort by cost "You make as few mattes as possible (because they are more expensive)"
                        .findFirst();

        final String solution;
        if (cheapestSolution.isPresent()) {
            solution = cheapestSolution.get().toString();
        } else {
            solution = NO_SOLUTION_FOUND;
        }
        long endTime = System.nanoTime();
        long durationNs = endTime - startTime;//ns = nanoseconds (/1_000_000 to get ms)
        LOGGER.info("{\"elapsed_ns\": {},\"nbpaints\":{},\"solution\":\"{}\"}",
                durationNs, solver.getNbPaints(), solution);

        return solution;
    }


    /**
     * @return the problem definition from the given file
     */
    List<String> problemDefinition() {
        try {
            return Files.readAllLines(this.input.toPath(), DEFAULT_CHARSET);
        } catch (IOException e) {
            throw new PaintShopInputRuntimeException(
                    PaintShopError.INVALID_INPUT_FILE,
                    "Check that you can read the input file as " + DEFAULT_CHARSET, e);
        }
    }

}
