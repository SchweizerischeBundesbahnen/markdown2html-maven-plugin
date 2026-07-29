package ch.sbb.maven.plugins.markdown2html.html;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
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

    public @NotNull String embed(@NotNull String html) {
        return "<style>\n%s</style>\n<div class=\"%s\">\n%s</div>\n".formatted(readStylesheet(), CONTENT_CLASS, html);
    }

    private static @NotNull String readStylesheet() {
        try (InputStream stylesheet = StylesheetEmbedder.class.getClassLoader().getResourceAsStream(STYLESHEET)) {
            return new String(Objects.requireNonNull(stylesheet, STYLESHEET + " is missing from the plugin").readAllBytes(),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + STYLESHEET, e);
        }
    }
}
