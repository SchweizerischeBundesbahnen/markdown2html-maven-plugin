package ch.sbb.maven.plugins.markdown2html.highlight;

import lombok.extern.slf4j.Slf4j;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.text.BadLocationException;
import java.util.Locale;
import java.util.Map;

/**
 * Turns the body of a fenced code block into HTML with the syntax coloured in.
 * <p>
 * github.com colours code with Linguist, server side, and dresses the result in class names that mean
 * nothing outside a GitHub page. Here the lexers of
 * <a href="https://github.com/bobbylight/RSyntaxTextArea">RSyntaxTextArea</a> do the tokenizing at build
 * time and each token gets its colour inline, so the generated file needs no stylesheet of its own to be
 * readable wherever it is embedded.
 * <p>
 * Only the lexers are used - no Swing component is ever created, and nothing here needs a display.
 */
@Slf4j
public class CodeHighlighter {

    /**
     * The language names that may appear after the opening fence, mapped to the lexer that handles them.
     * A block whose language is missing or not listed here is left uncoloured.
     */
    private static final Map<String, String> LANGUAGES = Map.ofEntries(
            Map.entry("bash", SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL),
            Map.entry("bat", SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH),
            Map.entry("c", SyntaxConstants.SYNTAX_STYLE_C),
            Map.entry("cmd", SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH),
            Map.entry("console", SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL),
            Map.entry("cpp", SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS),
            Map.entry("cs", SyntaxConstants.SYNTAX_STYLE_CSHARP),
            Map.entry("csharp", SyntaxConstants.SYNTAX_STYLE_CSHARP),
            Map.entry("css", SyntaxConstants.SYNTAX_STYLE_CSS),
            Map.entry("csv", SyntaxConstants.SYNTAX_STYLE_CSV),
            Map.entry("dart", SyntaxConstants.SYNTAX_STYLE_DART),
            Map.entry("docker", SyntaxConstants.SYNTAX_STYLE_DOCKERFILE),
            Map.entry("dockerfile", SyntaxConstants.SYNTAX_STYLE_DOCKERFILE),
            Map.entry("go", SyntaxConstants.SYNTAX_STYLE_GO),
            Map.entry("golang", SyntaxConstants.SYNTAX_STYLE_GO),
            Map.entry("groovy", SyntaxConstants.SYNTAX_STYLE_GROOVY),
            Map.entry("html", SyntaxConstants.SYNTAX_STYLE_HTML),
            Map.entry("ini", SyntaxConstants.SYNTAX_STYLE_INI),
            Map.entry("java", SyntaxConstants.SYNTAX_STYLE_JAVA),
            Map.entry("javascript", SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT),
            Map.entry("js", SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT),
            Map.entry("json", SyntaxConstants.SYNTAX_STYLE_JSON),
            Map.entry("jsp", SyntaxConstants.SYNTAX_STYLE_JSP),
            Map.entry("kotlin", SyntaxConstants.SYNTAX_STYLE_KOTLIN),
            Map.entry("kt", SyntaxConstants.SYNTAX_STYLE_KOTLIN),
            Map.entry("less", SyntaxConstants.SYNTAX_STYLE_LESS),
            Map.entry("lua", SyntaxConstants.SYNTAX_STYLE_LUA),
            Map.entry("make", SyntaxConstants.SYNTAX_STYLE_MAKEFILE),
            Map.entry("makefile", SyntaxConstants.SYNTAX_STYLE_MAKEFILE),
            Map.entry("perl", SyntaxConstants.SYNTAX_STYLE_PERL),
            Map.entry("php", SyntaxConstants.SYNTAX_STYLE_PHP),
            Map.entry("properties", SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE),
            Map.entry("proto", SyntaxConstants.SYNTAX_STYLE_PROTO),
            Map.entry("protobuf", SyntaxConstants.SYNTAX_STYLE_PROTO),
            Map.entry("py", SyntaxConstants.SYNTAX_STYLE_PYTHON),
            Map.entry("python", SyntaxConstants.SYNTAX_STYLE_PYTHON),
            Map.entry("rb", SyntaxConstants.SYNTAX_STYLE_RUBY),
            Map.entry("ruby", SyntaxConstants.SYNTAX_STYLE_RUBY),
            Map.entry("rs", SyntaxConstants.SYNTAX_STYLE_RUST),
            Map.entry("rust", SyntaxConstants.SYNTAX_STYLE_RUST),
            Map.entry("scala", SyntaxConstants.SYNTAX_STYLE_SCALA),
            Map.entry("sh", SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL),
            Map.entry("shell", SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL),
            Map.entry("sql", SyntaxConstants.SYNTAX_STYLE_SQL),
            // RSyntaxTextArea has no TOML lexer; the INI one gets comments, keys and strings right
            Map.entry("toml", SyntaxConstants.SYNTAX_STYLE_INI),
            Map.entry("ts", SyntaxConstants.SYNTAX_STYLE_TYPESCRIPT),
            Map.entry("typescript", SyntaxConstants.SYNTAX_STYLE_TYPESCRIPT),
            Map.entry("xml", SyntaxConstants.SYNTAX_STYLE_XML),
            Map.entry("yaml", SyntaxConstants.SYNTAX_STYLE_YAML),
            Map.entry("yml", SyntaxConstants.SYNTAX_STYLE_YAML),
            Map.entry("zsh", SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL)
    );

    /** Creates a highlighter. It keeps nothing between the blocks it is given. */
    public CodeHighlighter() {
        // Nothing to set up
    }

    /**
     * Colours a code block, or leaves it as it is when nothing can be made of its language.
     *
     * @param code     the body of the code block, exactly as written
     * @param language the language after the opening fence, if any
     * @return the code as HTML - coloured when the language is one of the known ones, plain otherwise
     */
    public @NotNull String highlight(@NotNull String code, @Nullable String language) {
        String syntaxStyle = language == null ? null : LANGUAGES.get(language.toLowerCase(Locale.ROOT));
        if (syntaxStyle == null) {
            return escape(code);
        }

        try {
            return colorize(code, syntaxStyle);
        } catch (BadLocationException | RuntimeException e) {
            log.warn("Could not highlight a '{}' code block, leaving it as it is", language, e);
            return escape(code);
        }
    }

    private @NotNull String colorize(@NotNull String code, @NotNull String syntaxStyle) throws BadLocationException {
        RSyntaxDocument document = new RSyntaxDocument(syntaxStyle);
        document.insertString(0, code, null);

        StringBuilder html = new StringBuilder(code.length());
        StringBuilder lexed = new StringBuilder(code.length());

        int lines = document.getDefaultRootElement().getElementCount();
        for (int line = 0; line < lines; line++) {
            if (line > 0) {
                html.append('\n');
                lexed.append('\n');
            }
            for (Token token = document.getTokenListForLine(line); token != null && token.isPaintable(); token = token.getNextToken()) {
                appendToken(html, lexed, token);
            }
        }

        // A lexer that dropped or rewrote part of the input would silently corrupt the code block. Falling
        // back to the plain rendering keeps the code intact, at the cost of its colours.
        if (!lexed.toString().equals(code)) {
            log.warn("Highlighting '{}' did not reproduce the code, leaving the block as it is", syntaxStyle);
            return escape(code);
        }
        return html.toString();
    }

    private void appendToken(@NotNull StringBuilder html, @NotNull StringBuilder lexed, @NotNull Token token) {
        String lexeme = token.getLexeme();
        lexed.append(lexeme);

        String color = HighlightTheme.colorOf(token.getType());
        if (color == null) {
            html.append(escape(lexeme));
        } else {
            html.append("<span style=\"color:").append(color).append("\">").append(escape(lexeme)).append("</span>");
        }
    }

    /**
     * Escapes what has to be escaped in element content. Double quotes are left alone, the way GitHub
     * leaves them.
     */
    private static @NotNull String escape(@NotNull String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
