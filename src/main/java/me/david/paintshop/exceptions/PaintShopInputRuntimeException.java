package me.david.paintshop.exceptions;

/**
 * RuntimeException used for issue related to the input file.
 * The issue can be related to the format of the file or the syntax of the input.
 */
public class PaintShopInputRuntimeException extends RuntimeException {

    public PaintShopInputRuntimeException(PaintShopError err, Throwable cause) {
        this(err, null, cause);
    }

    public PaintShopInputRuntimeException(PaintShopError err, String advice) {
        this(err, advice, null);
    }

    public PaintShopInputRuntimeException(PaintShopError err, String advice, Throwable cause) {
        super(concat(err, advice), cause);
    }

    private static String concat(PaintShopError err, String advice) {
        if (advice != null && !advice.isEmpty()) {
            return err.getDescription() + " - " + advice;
        }
        return err.getDescription();
    }
}
