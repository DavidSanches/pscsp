import exceptions.PaintShopError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;


class PaintShopErrorTest {

    @Test
    void errorEnumValueGiven_shouldHaveACodeAndADescription() {
        PaintShopError invalidArgs = PaintShopError.COMMAND_LINE_INVALID_ARGS;
        assertThat(invalidArgs.getCode()).isEqualTo(0);
        assertThat(invalidArgs.getDescription()).isEqualTo("Incorrect number of arguments. The program only accepts one argument, the input text file.");
    }

    @Test
    void errorEnumValueGiven_shouldHaveACustomStringRepresentation() {
        PaintShopError invalidArgs = PaintShopError.COMMAND_LINE_INVALID_ARGS;
        assertThat(invalidArgs.toString()).isEqualTo("0:" + invalidArgs.getDescription());
    }

    @Test
    void errorEnum_shouldHaveErorForInvalidNumberOfArgs() {
        assertThat(PaintShopError.valueOf("COMMAND_LINE_INVALID_ARGS"))
                .isNotNull();
    }

}