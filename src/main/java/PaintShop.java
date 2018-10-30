import exceptions.PaintShopError;

import java.io.FileNotFoundException;

/**
 * This is the main class for the PaintShop programming challenge solution.
 * You can launch it by calling the {@link #main} method with a valid
 * input file name as single argument.
 */
public class PaintShop {

    /**
     * Private constructor to avoid instantiation
     */
    private PaintShop() {
    }


    /**
     * This is the main method used to launch the program.
     * <p>The program accepts an input file as a command line argument, and prints a result to standard out.</p>
     * <p>Usage: <code>java -jar file-in.txt</code></p>
     * @param args
     */
    public static void main(String[] args) throws FileNotFoundException {
        if (args == null || args.length == 0 || args.length > 1) {
            PaintShopError error = PaintShopError.COMMAND_LINE_INVALID_ARGS;
            System.err.println(error.getDescription());
            System.exit(1);
        }

        String fileName = args[0];
        System.out.println(new PaintShopProblem(fileName)
                .solution());
        System.exit(0);
    }
}
