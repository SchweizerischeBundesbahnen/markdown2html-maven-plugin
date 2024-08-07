package ch.sbb.maven.plugins.markdown2html.html;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HtmlProcessorTest {

    public static Stream<Arguments> provideStringsForAddHeadingIds() {
        return Stream.of(
                Arguments.of("html/input.html", "html/expected/with_heading_ids.html")
        );
    }

    @ParameterizedTest
    @MethodSource("provideStringsForAddHeadingIds")
    void testAddHeadingIds(@NotNull String inputFilename, @NotNull String expectedOutputFilename) throws IOException {
        try (
                InputStream inputStream = HtmlProcessorTest.class.getClassLoader().getResourceAsStream(inputFilename);
                InputStream expectedInputStream = HtmlProcessorTest.class.getClassLoader().getResourceAsStream(expectedOutputFilename);
        ) {
            String html = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String result = new HtmlProcessor().addHeadingIds(html);
            String expectedOutput = new String(expectedInputStream.readAllBytes(), StandardCharsets.UTF_8);
            assertEquals(expectedOutput, result);
        }
    }
}