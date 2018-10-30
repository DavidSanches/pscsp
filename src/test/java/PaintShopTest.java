import org.junit.Ignore;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.*;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

//@Ignore
public class PaintShopTest {

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog();

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void testMainMethod_NoArgGiven_shouldWriteErrorMessageInSystemErr() throws FileNotFoundException {
        exit.expectSystemExitWithStatus(1);
        String[] args = null;
        PaintShop.main(args);
        assertThat(systemErrRule.getLogWithNormalizedLineSeparator())
                .isEqualTo(exceptions.PaintShopError.COMMAND_LINE_INVALID_ARGS.getDescription()+
                "\n");
    }

    @Test
    public void testMainMethod_validFilenameAsOnlyArgumentGiven_shouldWriteResultInSystemOut() throws FileNotFoundException {
        exit.expectSystemExitWithStatus(0);

        Path testResourcesPath = Paths.get("src", "test", "resources");
        String[] args = new String[]{testResourcesPath.resolve("example1.txt").toString()};
        PaintShop.main(args);
        String expected = "xxx";
        assertThat(systemOutRule.getLog()).isEqualTo(expected);
    }

}