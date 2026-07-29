package ch.sbb.maven.plugins.markdown2html.highlight;

import lombok.experimental.UtilityClass;
import org.fife.ui.rsyntaxtextarea.TokenTypes;
import org.jetbrains.annotations.Nullable;

/**
 * Maps the token types produced by the lexers to colours, so that highlighted code looks the way it looks on
 * github.com. The values are GitHub's light theme.
 * <p>
 * Only the token types that carry meaning are given a colour; identifiers, whitespace, separators and
 * operators keep the surrounding text colour, which also keeps the generated markup small.
 */
@UtilityClass
class HighlightTheme {

    private static final String COMMENT = "#6e7781";
    private static final String KEYWORD = "#cf222e";
    private static final String STRING = "#0a3069";
    private static final String CONSTANT = "#0550ae";
    private static final String ENTITY = "#8250df";
    private static final String VARIABLE = "#953800";
    private static final String TAG = "#116329";

    /**
     * @return the colour for a token type, or {@code null} when it should stay the default text colour
     */
    @Nullable
    String colorOf(int tokenType) {
        return switch (tokenType) {
            case TokenTypes.COMMENT_EOL,
                 TokenTypes.COMMENT_MULTILINE,
                 TokenTypes.COMMENT_DOCUMENTATION,
                 TokenTypes.COMMENT_MARKUP,
                 TokenTypes.MARKUP_COMMENT -> COMMENT;

            case TokenTypes.RESERVED_WORD,
                 TokenTypes.RESERVED_WORD_2,
                 TokenTypes.DATA_TYPE,
                 TokenTypes.PREPROCESSOR -> KEYWORD;

            case TokenTypes.LITERAL_STRING_DOUBLE_QUOTE,
                 TokenTypes.LITERAL_CHAR,
                 TokenTypes.LITERAL_BACKQUOTE,
                 TokenTypes.REGEX,
                 TokenTypes.MARKUP_TAG_ATTRIBUTE_VALUE,
                 TokenTypes.MARKUP_CDATA -> STRING;

            case TokenTypes.LITERAL_NUMBER_DECIMAL_INT,
                 TokenTypes.LITERAL_NUMBER_FLOAT,
                 TokenTypes.LITERAL_NUMBER_HEXADECIMAL,
                 TokenTypes.LITERAL_BOOLEAN,
                 TokenTypes.COMMENT_KEYWORD,
                 TokenTypes.MARKUP_TAG_ATTRIBUTE,
                 TokenTypes.MARKUP_ENTITY_REFERENCE,
                 TokenTypes.MARKUP_PROCESSING_INSTRUCTION,
                 TokenTypes.MARKUP_DTD -> CONSTANT;

            case TokenTypes.FUNCTION,
                 TokenTypes.ANNOTATION -> ENTITY;

            case TokenTypes.VARIABLE -> VARIABLE;

            case TokenTypes.MARKUP_TAG_NAME -> TAG;

            default -> null;
        };
    }
}
