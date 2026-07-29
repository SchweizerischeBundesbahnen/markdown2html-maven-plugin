package ch.sbb.maven.plugins.markdown2html.markdown;

import ch.sbb.maven.plugins.markdown2html.TestCommons;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MarkdownRendererTest {

    public static Stream<Arguments> render_markdown_returnsHtml_parameters() {
        return Stream.of(
                Arguments.of("", ""),
                Arguments.of("Hello, **world**!", "<p>Hello, <strong>world</strong>!</p>\n"),
                Arguments.of("# Façade", "<h1>Façade</h1>\n"),
                // A single newline is not a line break in a Markdown file
                Arguments.of("first\nsecond", "<p>first\nsecond</p>\n"),
                // Two trailing spaces are
                Arguments.of("first  \nsecond", "<p>first<br>\nsecond</p>\n"),
                // HTML5 void elements, not the XHTML "<hr />" commonmark-java writes by default
                Arguments.of("---", "<hr>\n"),
                // Double quotes stay literal in text, as they do in GitHub's output
                Arguments.of("say \"hi\"", "<p>say \"hi\"</p>\n")
        );
    }

    @ParameterizedTest
    @MethodSource("render_markdown_returnsHtml_parameters")
    void render_markdown_returnsHtml(String inputMarkdown, String expectedHtml) {
        assertEquals(expectedHtml, new MarkdownRenderer().render(inputMarkdown));
    }

    @Test
    void render_fixtureCoveringGfmConstructs_returnsExpectedHtml() {
        TestCommons.runFunctionTestUsingFiles("renderer/input.md", "renderer/expected.html",
                markdown -> new MarkdownRenderer().render(markdown));
    }
}
