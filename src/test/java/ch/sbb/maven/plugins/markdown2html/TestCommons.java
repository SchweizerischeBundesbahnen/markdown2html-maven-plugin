package ch.sbb.maven.plugins.markdown2html;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

@UtilityClass
public class TestCommons {

    @SneakyThrows
    public void runFunctionTestUsingFiles(String initialFilePath, String expectedFilePath, Function<String, String> function) {
        try (
                InputStream inputStream = Objects.requireNonNull(TestCommons.class.getClassLoader().getResourceAsStream(initialFilePath));
                InputStream expectedInputStream = Objects.requireNonNull(TestCommons.class.getClassLoader().getResourceAsStream(expectedFilePath));
        ) {
            String markdown = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String result = function.apply(markdown);
            String expectedOutput = new String(expectedInputStream.readAllBytes(), StandardCharsets.UTF_8);
            assertEquals(expectedOutput, result);
        }
    }

}
