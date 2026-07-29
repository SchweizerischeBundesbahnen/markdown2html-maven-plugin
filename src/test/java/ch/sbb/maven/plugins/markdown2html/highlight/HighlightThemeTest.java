package ch.sbb.maven.plugins.markdown2html.highlight;

import org.fife.ui.rsyntaxtextarea.TokenTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HighlightThemeTest {

    public static Stream<Arguments> colorOf_tokenType_returnsItsColor_parameters() {
        return Stream.of(
                Arguments.of(TokenTypes.COMMENT_EOL, "#6e7781"),
                Arguments.of(TokenTypes.RESERVED_WORD, "#cf222e"),
                Arguments.of(TokenTypes.LITERAL_STRING_DOUBLE_QUOTE, "#0a3069"),
                Arguments.of(TokenTypes.LITERAL_NUMBER_DECIMAL_INT, "#0550ae"),
                Arguments.of(TokenTypes.FUNCTION, "#8250df"),
                Arguments.of(TokenTypes.VARIABLE, "#953800"),
                Arguments.of(TokenTypes.MARKUP_TAG_NAME, "#116329")
        );
    }

    @ParameterizedTest
    @MethodSource("colorOf_tokenType_returnsItsColor_parameters")
    void colorOf_tokenType_returnsItsColor(int tokenType, String expectedColor) {
        assertEquals(expectedColor, HighlightTheme.colorOf(tokenType));
    }

    /**
     * The tokens that make up most of a code block keep the surrounding text colour, which is what keeps the
     * generated markup from being one span per word.
     */
    @Test
    void colorOf_tokenCarryingNoMeaning_returnsNoColor() {
        assertNull(HighlightTheme.colorOf(TokenTypes.IDENTIFIER));
        assertNull(HighlightTheme.colorOf(TokenTypes.WHITESPACE));
        assertNull(HighlightTheme.colorOf(TokenTypes.SEPARATOR));
        assertNull(HighlightTheme.colorOf(TokenTypes.OPERATOR));
    }
}
