package me.david.paintshop;

import me.david.paintshop.exceptions.PaintShopInputRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import static me.david.paintshop.exceptions.PaintShopError.INVALID_CUSTOMER_TASTE;

/**
 * Represents a customer taste, e.g. "1 G 2 M 4G"
 * <p>A taste is invalid if the paint reference is &gt; the <code>numberOfPaints</code>
 * parameter.</p>
 * <p>No customer will like more than one color in matte.</p>
 * <p>Tries to be as flexible as possible for the input. See unit test</p>
 * <p>A CustomeTaste contains 'paintReferences' a set of {@link PaintReference}</p>
 */
public class CustomerTaste {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerTaste.class);

    private final int numberOfPaints;
    private final String representation;
    private final Set<PaintReference> paintReferences;

    public CustomerTaste(int numberOfPaints, String taste) {
        this.numberOfPaints = numberOfPaints;
        this.representation = taste;

        try {
            this.paintReferences = Arrays
                    .stream(this.representation.split("(?<=[M|G])"))
                    .map(pntRef -> {
                        String trimmed = pntRef.trim();
                        int length = trimmed.length();
                        return new PaintReference(
                                trimmed.substring(0, length - 1).trim(),
                                trimmed.substring(length - 1));
                    })
                    .collect(Collectors.toSet());

        } catch (Exception exception) {
            LOGGER.info("Error parsing customer taste: {}", taste, exception);
            throw new PaintShopInputRuntimeException(
                    INVALID_CUSTOMER_TASTE,
                    String.format("Customer taste '%s' is not valid.", taste));
        }

        this.validate();
    }

    /**
     * Validates the customer taste:
     * <ul>
     * <li>...against the number of paints</li>
     * <li>No customer will like more than one color in matte.</li>
     * </ul>
     * This method is <code>final</code> as it is called from the constructor.
     */
    private final void validate() {
        int nbMattes = 0;
        for (PaintReference paintReference : this.paintReferences) {
            if (paintReference.index() > numberOfPaints) {
                throw new PaintShopInputRuntimeException(
                        INVALID_CUSTOMER_TASTE,
                        String.format("Customer taste '%s' is not valid. It references an unknown paint: '%s' (> %s).",
                                representation, paintReference.index(), numberOfPaints));
            }
            if (paintReference.finish().equals(PaintFinish.M)) {
                nbMattes += 1;
            }
            if (nbMattes > 1) {
                throw new PaintShopInputRuntimeException(
                        INVALID_CUSTOMER_TASTE,
                        String.format("Customer taste '%s' is not valid. More than one Matte finish detected.",
                                representation));
            }
        }
    }


    public Set<PaintReference> paintReferences() {
        return paintReferences;
    }

    /**
     * Return the number of paint references the customer has.
     * <p>This is used for sorting the customer tastes and
     * prune the paint combination as much and as early as
     * possible during the search.</p>
     *
     * @return the count of paint references the customer has
     */
    public int count() {
        return paintReferences.size();
    }

    /**
     * Just return the input string representation of the customer taste.
     *
     * @return the input string representation given as constructor parameter
     */
    @Override
    public String toString() {
        return this.paintReferences
                .stream()
                .sorted(Comparator.comparing(PaintReference::index))
                .map(ref -> ref.index() + " " + ref.finish().name())
                .collect( Collectors.joining( " " ) );
    }

    /**
     * Returns <code>true</code> if the current customer taste is satisfied by
     * the combination of paints., i.e. returns <code>true</code> as soon as
     * a paint (+finish) matches the predicate.
     * The length of the combination must match the {@link #numberOfPaints}
     *
     * @param combination String representation of a combination of paints (e.g. GGGM)
     * @return true if it is satisfied, else false
     */
    public boolean likes(String combination) {
        if (combination.length() != this.numberOfPaints) {
            return false;
        }
        return this.paintReferences.stream()
                .anyMatch(ref -> ref.finish().matches(combination.charAt(ref.index() - 1)));
    }
}
