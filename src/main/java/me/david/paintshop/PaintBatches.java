package me.david.paintshop;

import java.util.Arrays;

/**
 * This class manages String representation of a paint batches solution.
 * Used for getting the cost the paints, based on each {@link PaintFinish#cost()}
 */
public class PaintBatches {
    /**
     * This is the <strong>short</strong> string representation of the paint batches.
     * <br>E.g. 'GGG, 'GGM'
     */
    private final String finishesRepresentation;

    public PaintBatches(String finishesRepresentation) {
        this.finishesRepresentation = finishesRepresentation;
    }

    /**
     * @return a cost for the paint batches
     */
    public int cost() {
        return Arrays
                .stream(this.finishesRepresentation.split(""))
                .filter(s -> !" ".equals(s))
                .map(PaintFinish::valueOf)
                .mapToInt(PaintFinish::cost)
                .sum();
    }

    /**
     * Returns the paint combination formatted as expected.
     * <br>E.g. 'GGGGM' -&gt; 'G G G G M'
     *
     * @return the combination in the expected format
     */
    @Override
    public String toString() {
        return String.join(" ",
                this.finishesRepresentation.split(""));
    }
}
