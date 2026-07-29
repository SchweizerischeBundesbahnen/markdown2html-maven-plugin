package ch.sbb.maven.plugins.markdown2html.highlight;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedConstruction;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

class CodeHighlighterTest {

    private final CodeHighlighter highlighter = new CodeHighlighter();

    public static Stream<Arguments> highlight_knownLanguage_colorsTokens_parameters() {
        return Stream.of(
                Arguments.of("java", "int x;", "<span style=\"color:#cf222e\">int</span> x;"),
                Arguments.of("java", "// hi", "<span style=\"color:#6e7781\">// hi</span>"),
                // Language names are matched case-insensitively, and the aliases share a lexer
                Arguments.of("JAVA", "int x;", "<span style=\"color:#cf222e\">int</span> x;"),
                Arguments.of("yml", "# hi", "<span style=\"color:#6e7781\"># hi</span>"),
                Arguments.of("sh", "# hi", "<span style=\"color:#6e7781\"># hi</span>"),
                Arguments.of("xml", "<a>t</a>",
                        "&lt;<span style=\"color:#116329\">a</span>&gt;t&lt;/<span style=\"color:#116329\">a</span>&gt;")
        );
    }

    @ParameterizedTest
    @MethodSource("highlight_knownLanguage_colorsTokens_parameters")
    void highlight_knownLanguage_colorsTokens(String language, String code, String expectedHtml) {
        assertEquals(expectedHtml, highlighter.highlight(code, language));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "brainfuck", "not a language"})
    void highlight_unknownLanguage_returnsEscapedCode(String language) {
        assertEquals("a &lt; b &amp;&amp; c &gt; d", highlighter.highlight("a < b && c > d", language));
    }

    @Test
    void highlight_codeWithMarkupCharacters_escapesThem() {
        String html = highlighter.highlight("String s = \"<a> & </a>\";", "java");

        assertTrue(html.contains("&lt;a&gt; &amp; &lt;/a&gt;"), html);
        // Double quotes stay literal in element content, the way GitHub leaves them
        assertTrue(html.contains("\"&lt;a&gt;"), html);
    }

    /**
     * No input found by probing the lexers makes them fail, so the two ways a block can fall back to the
     * plain rendering are provoked here. Both matter more than the colours: a reader copies the code.
     */
    @Test
    void highlight_lexerFails_returnsEscapedCode() {
        try (MockedConstruction<RSyntaxDocument> ignored = mockConstruction(RSyntaxDocument.class,
                (document, context) -> doThrow(new BadLocationException("no such place", 0))
                        .when(document).insertString(anyInt(), anyString(), any()))) {

            assertEquals("a &lt; b", highlighter.highlight("a < b", "java"));
        }
    }

    @Test
    void highlight_lexerDoesNotReproduceTheCode_returnsEscapedCode() {
        try (MockedConstruction<RSyntaxDocument> ignored = mockConstruction(RSyntaxDocument.class,
                (document, context) -> {
                    Element root = mock(Element.class);
                    when(root.getElementCount()).thenReturn(1);
                    when(document.getDefaultRootElement()).thenReturn(root);
                    // A lexer that hands back nothing has swallowed the whole line
                    when(document.getTokenListForLine(0)).thenReturn(null);
                })) {

            assertEquals("a &lt; b", highlighter.highlight("a < b", "java"));
        }
    }

    /**
     * Whatever the lexer makes of a block, the code itself has to come out of it unchanged - that is what
     * the reader ends up copying into a terminal.
     */
    @ParameterizedTest
    @ValueSource(strings = {"java", "bash", "xml", "yaml", "json", "python", "sql", "properties"})
    void highlight_anyLanguage_preservesTheCode(String language) {
        String code = """
                first line
                  indented "quoted" <tagged> & escaped
                \ttab indented

                after an empty line
                """;

        String text = highlighter.highlight(code, language)
                .replaceAll("<[^>]*>", "")
                .replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&");

        assertEquals(code, text);
    }
}
