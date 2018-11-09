package me.david.paintshop;

import me.david.paintshop.exceptions.PaintShopInputRuntimeException;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static me.david.paintshop.PaintFinish.G;
import static me.david.paintshop.PaintFinish.M;
import static me.david.paintshop.exceptions.PaintShopError.INVALID_INPUT_FILE_NUMBER_OF_PAINTS;
import static org.assertj.core.api.Java6Assertions.assertThat;

class PaintShopProblemTest {
    private final Path testResourcesPath = Paths.get("src", "test", "resources");

    @Test
    void example1Given_shouldFindExpectedSolution1() throws IOException {
        testWithFileInAndExpectedContentFile("example1.txt",
                "example1_expected.txt");
    }

    @Test
    void example2Given_shouldFindExpectedSolution2() throws IOException {
        testWithFileInAndExpectedContentFile("example2.txt",
                "example2_expected.txt");
    }

    @Test
    void example3Given_shouldFindExpectedSolution3() throws IOException {
        testWithFileInAndExpectedContentFile("example3.txt",
                "example3_expected.txt");
    }

    @Test
    void example4Given_shouldFindExpectedSolution4() throws IOException {
        testWithFileInAndExpectedContentFile("example4.txt",
                "example4_expected.txt");
    }


    @Test
    void example5Given_shouldFindExpectedSolution5() throws IOException {
        testWithFileInAndExpectedContentFile("example5.txt",
                "example5_expected.txt");
    }
    @Test
    void example5alternativeGiven_shouldFindExpectedSolution5alternative() throws IOException {
        testWithFileInAndExpectedContentFile("example5alternative.txt",
                "example5alternative_expected.txt");
    }

    private void testWithFileInAndExpectedContentFile(String fin, String fexpected) throws IOException {
        Path input = testResourcesPath.resolve(fin);
        Path expectedFileName = testResourcesPath.resolve(fexpected);

        String expected = Files
                .lines(expectedFileName)
                .findFirst()
                .get();
        String solution = new PaintShopProblem(input.toString())
                .solution();
        assertThat(solution).isEqualTo(expected);
    }

    @Test
    void inputFileGiven_shouldTransformLinesIntoList() throws FileNotFoundException {
        Path input = testResourcesPath.resolve("example1.txt");

        Deque<String> inputLines = new PaintShopProblem(input.toString())
                .problemDefinition();
        assertThat(inputLines).contains(
                "5",
                "1 M 3 G 5 G",
                "2 G 3 M 4 G",
                "5 M");
    }

    @Test
    void testSolutions_customerTastesNotIncludingAllBatches_shouldReturnOneBatchForEachColor() throws IOException {
        testWithFileInAndExpectedContentFile("example-not-all-paint-expressed.txt",
                "example-not-all-paint-expressed_expected.txt");
    }

    @Test
    void testSolutions_example$alternative() throws IOException {
        testWithFileInAndExpectedContentFile("example4alternative.txt",
                "example4alternative_expected.txt");
    }
    @Test
    void testSolutions_example$alternative2() throws IOException {
        testWithFileInAndExpectedContentFile("example4alternative2.txt",
                "example4alternative2_expected.txt");
    }


    @Test
    void testParseNbPaints_notANumberGiven_shouldThrowAPaintShopInputRuntimeException() {
        try {
            Path input = testResourcesPath.resolve("example1.txt");

            PaintShopProblem problemDefinition = new PaintShopProblem(input.toString());
            problemDefinition.parseNbPaints("a");

        } catch (Exception e) {
            assertThat(e)
                    .isInstanceOf(PaintShopInputRuntimeException.class)
                    .hasMessage(INVALID_INPUT_FILE_NUMBER_OF_PAINTS.getDescription() +
                            " - First line 'a' is expected to be an integer.")
                    .hasCauseInstanceOf(NumberFormatException.class);
        }
    }
}