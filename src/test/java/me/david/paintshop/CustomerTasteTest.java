package me.david.paintshop;

import me.david.paintshop.exceptions.PaintShopInputRuntimeException;
import me.david.paintshop.CustomerTaste;
import me.david.paintshop.PaintFinish;
import me.david.paintshop.PaintReference;
import org.junit.jupiter.api.Test;

import static me.david.paintshop.exceptions.PaintShopError.INVALID_CUSTOMER_TASTE;
import static org.assertj.core.api.Java6Assertions.assertThat;


class CustomerTasteTest {
    @Test
    void testParse_stringTasteGiven_shouldValidateAndParsePaintReference() {
        CustomerTaste customerTaste = new CustomerTaste(5, "2G3M");

        assertThat(customerTaste.toString()).isEqualTo("2G3M");
        assertThat(customerTaste.paintReferences())
                .contains(new PaintReference("2", "G"),
                        new PaintReference("3", "M"));
    }

    @Test
    void testParse_invalidPaintIndexGiven_shouldThrownException() {
        try {
            CustomerTaste customerTaste = new CustomerTaste(5, "2G6M");
        } catch (Exception e) {
            assertThat(e)
                    .isInstanceOf(PaintShopInputRuntimeException.class)
                    .hasMessage(INVALID_CUSTOMER_TASTE.getDescription() +
                            " - Customer taste '2G6M' is not valid. It references an unknown paint: '6' (> 5).")
                    .hasNoCause();
        }
    }

    @Test
    void testParse_moreThanOneMatteGiven_shouldThrownException() {
        try {
            CustomerTaste customerTaste = new CustomerTaste(5, "1M2G3G4M");
        } catch (Exception e) {
            assertThat(e)
                    .isInstanceOf(PaintShopInputRuntimeException.class)
                    .hasMessage(INVALID_CUSTOMER_TASTE.getDescription() +
                            " - Customer taste '1M2G3G4M' is not valid. More than one Matte finish detected.")
                    .hasNoCause();
        }
    }

    @Test
    void testParse_validRepresentationsGiven() {
        assertThat(new CustomerTaste(5, "1 M").paintReferences())
                .contains(new PaintReference(1, PaintFinish.M));
        assertThat(new CustomerTaste(5, "1M2G").paintReferences())
                .contains(new PaintReference(1, PaintFinish.M),
                        new PaintReference(2, PaintFinish.G));

        //double digits paint indices
        assertThat(new CustomerTaste(11, "10M11 G").paintReferences())
                .contains(new PaintReference(10, PaintFinish.M),
                        new PaintReference(11, PaintFinish.G));
    }

    @Test
    void testParse_invalidRepresentationsGiven_shouldThrowException() {
        try {
            new CustomerTaste(3, "4M").paintReferences();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(PaintShopInputRuntimeException.class);
        }

        try {
            new CustomerTaste(11, "1 M 10G 11M 12G").paintReferences();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(PaintShopInputRuntimeException.class);
        }
    }


    @Test
    void testValidCustomerTaste_invalidTasteRepresentationGiven_shouldThrowException() {
        try {
            new CustomerTaste(3, "invalid format");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(PaintShopInputRuntimeException.class);
        }
    }

    @Test
    void testCount_shouldReturnTheNumberOfPaintsTheCustomerLikes() {
        CustomerTaste customerTaste = new CustomerTaste(5, "2G3M");
        assertThat(customerTaste.count()).isEqualTo(2);
    }

    @Test
    void testToString_shouldReturnTheInputStringRepresentation() {
        CustomerTaste customerTaste = new CustomerTaste(5, "2G3M");
        assertThat(customerTaste.toString()).isEqualTo("2G3M");
    }

    @Test
    void testLike_invalidLengthForPaintCombinationGiven_shouldReturnFalse() {
        CustomerTaste customerTaste = new CustomerTaste(5, "2G3M");
        assertThat(customerTaste.likes("GGMG")).isFalse(); //not the correct size!!
    }

    @Test
    void testLike_satisfyingPaintCombinationGiven_shouldReturnTrue() {
        CustomerTaste customerTaste = new CustomerTaste(5, "2G3M");
        assertThat(customerTaste.likes("GGMGG")).isTrue(); //notice the correct size
    }

}