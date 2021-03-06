package me.david.paintshop.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Unit test fpr {@link PaintShopInputRuntimeException}
 */
class PaintShopInputRuntimeExceptionTest {

    @Test
    void exceptionWithAdviceGiven_shouldConcatenateTheMessageAndErrorDescription() {
        PaintShopError error = PaintShopError.COMMAND_LINE_INVALID_ARGS;
        String someAdvice = "some advice";
        Throwable t = new Throwable("fake throwable");
        Exception exception = new PaintShopInputRuntimeException(
                error, someAdvice, t);
        assertThat(exception.getMessage()).isEqualTo(error.getDescription() + " - some advice");
        assertThat(exception).hasCause(t);
    }

    @Test
    void exceptionWithNoAdviceGiven_shouldConcatenateTheMessageAndErrorDescription() {
        PaintShopError error = PaintShopError.COMMAND_LINE_INVALID_ARGS;
        Throwable t = new Throwable("fake throwable");
        Exception exception = new PaintShopInputRuntimeException(error, t);
        assertThat(exception.getMessage()).isEqualTo(error.getDescription());
        assertThat(exception).hasCause(t);
    }

}