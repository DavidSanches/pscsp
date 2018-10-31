package me.david.paintshop;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * Represents one paint reference.
 * Composed of a paint index (1 based) and a {@link PaintFinish}
 */
@EqualsAndHashCode
@AllArgsConstructor
public class PaintReference {

    /**
     * One-based index of paint (e.g. 1 for paint #1, 2 for paint #2)
     */
    private final int index;
    private final PaintFinish finish;

    public PaintReference(String index, String finish) {
        this(Integer.valueOf(index), PaintFinish.valueOf(finish));
    }


    public int index() {
        return index;
    }

    public PaintFinish finish() {
        return finish;
    }

}
