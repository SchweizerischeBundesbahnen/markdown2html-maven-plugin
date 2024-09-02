package ch.sbb.maven.plugins.markdown2html.markdown;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MarkdownProcessorTest {

    public static final String INPUT_MD = "markdown/input.md";

    private static Stream<Arguments> provideStringsForRemoveChapterFromInputMarkdown() {
        return Stream.of(
                Arguments.of(INPUT_MD, null, INPUT_MD),
                Arguments.of(INPUT_MD, Collections.emptyList(), INPUT_MD),
                Arguments.of(INPUT_MD, List.of("# Non-Exising Chapter"), INPUT_MD),
                Arguments.of(INPUT_MD, List.of("Chapter name without leading level"), INPUT_MD),
                Arguments.of(INPUT_MD, List.of("# Polarion configuration"), INPUT_MD),
                Arguments.of(INPUT_MD, List.of("### Polarion configuration"), INPUT_MD),

                Arguments.of(INPUT_MD, List.of("## Build"), "markdown/expected/without_build.md"),
                Arguments.of(INPUT_MD, List.of("## Build", "## Installation to Polarion"), "markdown/expected/without_build_and_installation.md"),
                Arguments.of(INPUT_MD, List.of("## Build", "## Installation to Polarion", "## Changelog"), "markdown/expected/without_build_installation_and_changelog.md"),
                Arguments.of(INPUT_MD, List.of("## Build", "# Non-Exising Chapter"), "markdown/expected/without_build.md"),

                Arguments.of(INPUT_MD, List.of("# Extension Name"), "markdown/expected/empty.md"),

                Arguments.of(INPUT_MD, List.of("### Configuration 2"), "markdown/expected/without_configuration2.md"),
                Arguments.of(INPUT_MD, List.of("### Configuration 1", "### Configuration 2"), "markdown/expected/without_configuration1_and_configuration2.md"),

                Arguments.of(INPUT_MD, List.of("### Configuration 1", "## Polarion configuration"), "markdown/expected/without_polarion_configuration.md"),
                Arguments.of(INPUT_MD, List.of("## Polarion configuration", "### Configuration 1"), "markdown/expected/without_polarion_configuration.md")
        );
    }

    @ParameterizedTest
    @MethodSource("provideStringsForRemoveChapterFromInputMarkdown")
    void testRemoveChapterFromInputMarkdown(@NotNull String inputFilename, @Nullable List<String> excludeChapters, @NotNull String expectedOutputFilename) throws IOException {
        try (
                InputStream inputStream = MarkdownProcessorTest.class.getClassLoader().getResourceAsStream(inputFilename);
                InputStream expectedInputStream = MarkdownProcessorTest.class.getClassLoader().getResourceAsStream(expectedOutputFilename);
        ) {
            String markdown = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String result = new MarkdownProcessor().removeChapter(markdown, excludeChapters);
            String expectedOutput = new String(expectedInputStream.readAllBytes(), StandardCharsets.UTF_8);
            assertEquals(expectedOutput, result);
        }
    }
}