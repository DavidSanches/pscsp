package me.david.paintshop;

/**
 * Represents one paint reference.
 * Composed of a paint index (1 based) and a {@link PaintFinish}
 */
public class PaintReference {

    /**
     * One-based index of paint (e.g. 1 for paint #1, 2 for paint #2)
     */
    private final int index;
    private final PaintFinish finish;

    public PaintReference(String index, String finish) {
        this(Integer.valueOf(index), PaintFinish.valueOf(finish));
    }
    public PaintReference(int index, PaintFinish finish) {
        this.index = index;
        this.finish = finish;
    }

    public int index() {
        return index;
    }

    public PaintFinish finish() {
        return finish;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PaintReference that = (PaintReference) o;

        if (index != that.index) return false;
        return finish == that.finish;
    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + (finish != null ? finish.hashCode() : 0);
        return result;
    }
}
