package ch.sbb.maven.plugins.markdown2html.html;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Wraps the rendered markup in GitHub's own markdown stylesheet, so that a page embedding the generated file
 * gets GitHub's typography - headings, tables, blockquotes, the frame around code blocks - without having to
 * provide any styles of its own.
 * <p>
 * The stylesheet is <a href="https://github.com/sindresorhus/github-markdown-css">github-markdown-css</a>,
 * vendored into the plugin so that nothing is fetched at build time. Every rule in it is scoped to
 * {@code .markdown-body}, the class the content is wrapped in, so nothing bleeds into the surrounding page.
 */
public class StylesheetEmbedder {

    private static final String STYLESHEET = "github-markdown-light.css";
    private static final String CONTENT_CLASS = "markdown-body";

    /** Creates an embedder. The stylesheet is read from the plugin on each call. */
    public StylesheetEmbedder() {
        // Nothing to set up
    }

    /**
     * The newlines are HTML the generated file carries, not console output, so they stay {@code \n} on
     * every platform - which is also why this is concatenated rather than formatted.
     *
     * @param html the rendered document
     * @return the document wrapped in the stylesheet and in the class its rules are scoped to
     * @throws IOException if the stylesheet cannot be read out of the plugin
     */
    public @NotNull String embed(@NotNull String html) throws IOException {
        return "<style>\n" + readStylesheet() + "</style>\n"
                + "<div class=\"" + CONTENT_CLASS + "\">\n" + html + "</div>\n";
    }

    private static @NotNull String readStylesheet() throws IOException {
        try (InputStream stylesheet = StylesheetEmbedder.class.getClassLoader().getResourceAsStream(STYLESHEET)) {
            return new String(Objects.requireNonNull(stylesheet, STYLESHEET + " is missing from the plugin").readAllBytes(),
                    StandardCharsets.UTF_8);
        }
    }
}
