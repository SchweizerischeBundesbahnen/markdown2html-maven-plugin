package ch.sbb.maven.plugins.markdown2html.github;

import ch.sbb.maven.plugins.markdown2html.markdown.MarkdownRenderer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Cross-checks the local renderer against GitHub's own markdown API: the fixture is rendered locally and
 * remotely and the two results are compared.
 * <p>
 * Both tests call api.github.com and abort with a message if it cannot be reached, so a build without
 * network access still passes - the offline guarantee for the renderer is
 * {@link ch.sbb.maven.plugins.markdown2html.markdown.MarkdownRendererTest}.
 * <p>
 * The API is asked for mode {@code markdown}, the one that renders a Markdown <em>file</em> the way
 * README.md is rendered on the repository page. Its other mode, {@code gfm}, renders issue and pull request
 * comments: it turns every single newline into a {@code <br>}, which is not how a README reads.
 */
class GitHubParityTest {

    /**
     * The fixture covers everything GitHub returns without decorating it, and the comparison is on the raw
     * bytes - no normalization on either side.
     */
    @Test
    void render_constructsWithoutGitHubChrome_matchesGitHubByteForByte() {
        String markdown = readFixture("parity/strict.md");

        assertEquals(GitHubMarkdownApi.render(markdown, GitHubMarkdownApi.MARKDOWN),
                new MarkdownRenderer().render(markdown));
    }

    /**
     * Alerts are checked against mode {@code gfm}, the only mode that renders them - and the mode this
     * plugin asked for while it still called the API, which is what makes this the regression test for
     * having stopped.
     * <p>
     * The two put their newlines between block elements in different places, so both sides lose the ones
     * that sit between tags. Nothing else is touched: the class names, the octicon and the title all have
     * to match on the byte.
     */
    @Test
    void render_alerts_matchesGitHub() {
        String markdown = readFixture("parity/alerts.md");

        String expected = GitHubMarkdownApi.render(markdown, GitHubMarkdownApi.GFM);
        String actual = new MarkdownRenderer().render(markdown);

        assertEquals(betweenTagNewlinesRemoved(expected), betweenTagNewlinesRemoved(actual));
    }

    private static String betweenTagNewlinesRemoved(String html) {
        return html.replace(">\n<", "><").strip();
    }

    /**
     * The same for the constructs GitHub wraps in site markup - headings, highlighted code, proxied images,
     * footnotes - after {@link GitHubHtmlNormalizer} has taken that markup off both sides.
     * <p>
     * Two GFM constructs are deliberately missing from the fixture because the API gives nothing to compare
     * against: emoji shortcodes, which GitHub replaces with characters from an emoji database the renderer
     * does not carry, and task lists, which mode {@code markdown} does not implement at all (it returns the
     * literal {@code [ ]}) although GitHub does render them in files. Both are covered offline instead.
     */
    @Test
    void render_constructsWithGitHubChrome_matchesGitHubAfterNormalization() {
        String markdown = readFixture("parity/full.md");

        String expected = GitHubHtmlNormalizer.normalize(GitHubMarkdownApi.render(markdown, GitHubMarkdownApi.MARKDOWN));
        String actual = GitHubHtmlNormalizer.normalize(new MarkdownRenderer().render(markdown));

        assertEquals(expected, actual);
    }

    @SneakyThrows
    private String readFixture(String path) {
        try (InputStream inputStream = Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream(path), "Missing fixture: " + path)) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
