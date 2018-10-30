import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Deque;
import java.util.List;
import java.util.StringJoiner;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PaintShopProblemTest {
    Path testResourcesPath = Paths.get("src", "test", "resources");

    @Test
    void example1Given_shouldFindExpectedSolution1() throws IOException {
        testWithFileInAndExpectedContentFile("example1.txt",
                "example1_expected.txt");
    }
    @Test
    void example1Given_shouldFindExpectedSolution2() throws IOException {
        testWithFileInAndExpectedContentFile("example2.txt",
                "example2_expected.txt");
    }
    @Test
    void example1Given_shouldFindExpectedSolution3() throws IOException {
        testWithFileInAndExpectedContentFile("example3.txt",
                "example3_expected.txt");
    }
    @Test
    void example1Given_shouldFindExpectedSolution4() throws IOException {
        testWithFileInAndExpectedContentFile("example4.txt",
                "example4_expected.txt");
    }

    void testWithFileInAndExpectedContentFile(String fin, String fexpected) throws IOException {
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

        List<String> inputLines = new PaintShopProblem(input.toString())
                .problemDefinition();
        assertThat(inputLines).contains(
                "5",
                "1 M 3 G 5 G",
                "2 G 3 M 4 G",
                "5 M");
    }

}