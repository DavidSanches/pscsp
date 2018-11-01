package me.david.paintshop;

import me.david.paintshop.exceptions.PaintShopError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;

/**
 * This is the main class for the me.david.paintshop.PaintShop programming challenge solution.
 * You can launch it by calling the {@link #main} method with a valid
 * input file name as single argument.
 */
public class PaintShop {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaintShop.class);

    /**
     * Private constructor to avoid instantiation
     */
    private PaintShop() {
    }


    /**
     * This is the main method used to launch the program.
     * <p>The program accepts an input file as a command line argument, and prints a result to standard out.</p>
     * <p>Usage: <code>java -jar file-in.txt</code></p>
     *
     * @param args list of arguments. The program expect only one argument: a valid input file with number of paints
     *             and list of customer tastes (See definition of the problem in file README.md.
     */
    public static void main(String[] args) {
        if (args == null || args.length == 0 || args.length > 1) {
            PaintShopError error = PaintShopError.COMMAND_LINE_INVALID_ARGS;
            System.err.println(error.getDescription());
            return;
        }

        String fileName = args[0];
        final String solution;
        try {
            solution = new PaintShopProblem(fileName)
                    .solution();
            System.out.print(solution);
        } catch (FileNotFoundException exception) {
            LOGGER.error("Error running the PaintShop program: ", exception.getMessage());
            System.err.print(exception.getMessage());
        }
    }
}
