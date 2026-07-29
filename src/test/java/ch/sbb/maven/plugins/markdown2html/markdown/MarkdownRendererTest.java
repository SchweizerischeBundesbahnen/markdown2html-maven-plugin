package ch.sbb.maven.plugins.markdown2html.markdown;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
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
        assertEquals(readResource("renderer/expected.html"),
                new MarkdownRenderer().render(readResource("renderer/input.md")));
    }

    @Test
    void render_highlightingOff_leavesTheCodeUncoloured() {
        String markdown = "```java\nint x;\n```";

        assertEquals("<pre><code class=\"language-java\">int x;\n</code></pre>\n",
                new MarkdownRenderer(false).render(markdown));
    }

    @Test
    void render_highlightingOn_coloursTheCode() {
        String markdown = "```java\nint x;\n```";

        assertEquals("<pre><code class=\"language-java\"><span style=\"color:#cf222e\">int</span> x;\n</code></pre>\n",
                new MarkdownRenderer(true).render(markdown));
    }

    @SneakyThrows
    private String readResource(String path) {
        try (InputStream resource = Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream(path), "Missing resource: " + path)) {
            return new String(resource.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
