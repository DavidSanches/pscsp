package exceptions;

/**
 * Library of error code, i18n code and default english description
 */
public enum PaintShopError {
    COMMAND_LINE_INVALID_ARGS(0, "Incorrect number of arguments. " +
            "The program only accepts one argument, the input text file."),
    INVALID_INPUT_FILE(1, "Input file provided cannot be read."),
    INVALID_INPUT_FILE_FORMAT(2, "Input file provided has invalid format."),
    INVALID_INPUT_FILE_NUMBER_OF_PAINTS(3, "Invalid number of paints."),
    INVALID_CUSTOMER_TASTE(4, "Invalid color set customer taste."),
    INVALID_COLOR_SET(5, "Invalid color set format.")
    ;

    private final int code;
    private final String description;

    private PaintShopError(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code + ":" + description;
    }

}
