package me.david.paintshop;

/**
 * Used to manage the paint finish type: Gloss or Matte.
 * <p>Arbitrary cost is used for both, but Gloss is cheaper than Matte</p>
 */
public enum PaintFinish {

    G, M;

    PaintFinish() {
    }

    /**
     * @return an arbitrary integer cost of a finish type
     * (uses the {@link #ordinal()} as arbitrary cost
     */
    public int cost() {
        return this.ordinal();
    }

    /**
     * return true if this finish matches the character
     * (typically 'G', 'M') given.
     *
     * @param c character to match this finish against
     * @return true if it matches, else false
     */
    public boolean matches(char c) {
        return this.name().equals(String.valueOf(c));
    }
}
