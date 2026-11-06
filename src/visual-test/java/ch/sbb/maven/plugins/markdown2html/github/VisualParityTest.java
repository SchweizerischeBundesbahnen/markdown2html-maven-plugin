package ch.sbb.maven.plugins.markdown2html.github;

import ch.sbb.maven.plugins.markdown2html.html.StylesheetEmbedder;
import ch.sbb.maven.plugins.markdown2html.markdown.MarkdownRenderer;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Route;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.abort;

/**
 * Renders the fixture locally and through GitHub's markdown API, puts both results in the same page, and
 * compares screenshots of the two.
 * <p>
 * {@link GitHubParityTest} compares markup, which is exact but blind to what the difference does to a
 * reader: an extra wrapper that changes nothing on screen fails it, and a missing line break that moves half
 * the page does not have to. This test answers the other question - whether the two look the same.
 * <p>
 * Both pages are dressed in the stylesheet {@code embedStylesheet} ships and then in
 * {@code visual/neutralize.css}, which takes off the markup github.com adds around the rendering. Every rule
 * in that file corresponds to a difference the plugin's README documents; the test would not see a
 * regression hidden behind a rule that does not belong there, so nothing else may be added to it.
 * <p>
 * The test is not part of the normal build - it needs the {@code visual-parity} profile, since Playwright
 * downloads a browser the first time it runs. Even then it aborts rather than fails when the browser or the
 * GitHub API is unavailable.
 */
class VisualParityTest {

    private static final String FIXTURE = "parity/full.md";
    private static final int VIEWPORT_WIDTH = 1000;
    private static final int VIEWPORT_HEIGHT = 800;

    /**
     * Identical rendering is expected to be identical to the pixel; the allowance only covers sub-pixel
     * rounding of anti-aliased text, which cannot move anything a reader would notice.
     */
    private static final double ALLOWED_DIFFERING_RATIO = 0.0005;

    private static final Path ARTIFACTS = Path.of("target", "visual-parity");

    /**
     * The two differences that no stylesheet can take off, because they are in the markup rather than in
     * how it is painted:
     * <ul>
     *     <li>the trailing newline inside a code block, which GitHub's highlighted blocks do not carry and
     *     the local ones do - an empty last line in half the code blocks on the page;</li>
     *     <li>the class on the footnote section, which the API leaves off in mode {@code markdown} although
     *     github.com puts it there, so the stylesheet indents only one of the two.</li>
     * </ul>
     */
    private static final String EQUALIZE_MARKUP = """
            () => {
              for (const section of document.querySelectorAll('section[data-footnotes]')) {
                section.classList.add('footnotes');
              }
              for (const pre of document.querySelectorAll('pre')) {
                let node = pre;
                while (node.lastChild) { node = node.lastChild; }
                if (node.nodeType === Node.TEXT_NODE) { node.data = node.data.replace(/\\s+$/, ''); }
              }
            }
            """;

    @Test
    void render_fixture_looksTheSameAsGitHub() {
        String markdown = readResource(FIXTURE);
        String githubHtml = GitHubMarkdownApi.render(markdown, GitHubMarkdownApi.MARKDOWN);
        String localHtml = new MarkdownRenderer().render(markdown);

        try (Playwright playwright = createPlaywright()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newContext(new Browser.NewContextOptions()
                    .setViewportSize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                    .setDeviceScaleFactor(1))
                    .newPage();
            // Nothing may be fetched: the camo URLs GitHub returns are not the ones the local renderer
            // keeps, and a network round trip would make the screenshots depend on the day
            page.route("**", Route::abort);

            ScreenshotComparison comparison = ScreenshotComparison.of(screenshot(page, githubHtml), screenshot(page, localHtml));
            comparison.writeTo(ARTIFACTS);

            assertTrue(comparison.getDifferingRatio() <= ALLOWED_DIFFERING_RATIO,
                    () -> "The local rendering does not look like GitHub's: %s. See %s"
                            .formatted(comparison.describe(), ARTIFACTS.toAbsolutePath()));
        }
    }

    private static Playwright createPlaywright() {
        try {
            return Playwright.create();
        } catch (RuntimeException e) {
            return abort("Playwright cannot start, install its browser first "
                    + "(mvn -P visual-parity test-compile exec:java@install-browser): " + e);
        }
    }

    private byte[] screenshot(Page page, String html) {
        page.setContent(buildPage(html));
        page.evaluate(EQUALIZE_MARKUP);
        return page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
    }

    @SneakyThrows
    private String buildPage(String html) {
        return readResource("visual/shell.html")
                .replace("{{content}}", new StylesheetEmbedder().embed(html))
                .replace("{{neutralize}}", readResource("visual/neutralize.css"));
    }

    @SneakyThrows
    private String readResource(String path) {
        try (InputStream resource = Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream(path), "Missing resource: " + path)) {
            return new String(resource.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
