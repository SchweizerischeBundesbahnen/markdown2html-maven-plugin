package ch.sbb.maven.plugins.markdown2html.github;

import ch.sbb.maven.plugins.markdown2html.html.StylesheetEmbedder;
import ch.sbb.maven.plugins.markdown2html.markdown.MarkdownRenderer;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Route;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.abort;

/**
 * Records what the plugin's output looks like and holds it there.
 * <p>
 * {@link BaselineTest} keeps the markup still and says in text what moved; this keeps the picture still. It
 * is the only test that sees the syntax colours at all - the live comparison in {@link VisualParityTest}
 * has to switch them off, because Linguist and the local lexers cannot agree on them by construction - and
 * the only one that sees an alert rendered, since the API mode that test uses renders none.
 * <p>
 * Text rasterises differently on every platform, so the baseline is only meaningful in one: the Playwright
 * image pinned to the same version as the dependency. The {@code visual-parity} profile runs it there for
 * you; started anywhere else it aborts rather than fail on somebody's font stack. To take the pictures
 * again after an intended change:
 * <pre>
 * mvn -P visual-parity verify -Dvisual.baseline.update=true
 * </pre>
 * <p>
 * One picture per area - text, code, blocks - so that a failure names where to look, and so that no
 * recording grows past the size the large-file hook allows.
 */
class VisualBaselineTest {

    private static final Path BASELINES = Path.of("src", "visual-test", "resources", "baseline");
    private static final Path FAILURES = Path.of("target", "visual-baseline");

    private static final int VIEWPORT_WIDTH = 1000;
    private static final int VIEWPORT_HEIGHT = 800;

    /**
     * In one and the same container the rendering is reproducible to the pixel, so anything at all is a
     * change worth looking at.
     */
    private static final double ALLOWED_DIFFERING_RATIO = 0.0;

    @ParameterizedTest
    @ValueSource(strings = {"text", "code", "blocks"})
    @SneakyThrows
    void render_fixture_stillLooksLikeTheRecordedBaseline(String fixture) {
        if (!"1".equals(System.getenv("VISUAL_BASELINE_CONTAINER"))) {
            abort("The image baselines only hold in the pinned Playwright container, "
                    + "which mvn -P visual-parity verify runs them in");
        }

        byte[] screenshot = screenshot(new MarkdownRenderer().render(readResource("visual/baseline-" + fixture + ".md")));
        Path baseline = BASELINES.resolve(fixture + ".png");

        if (Boolean.getBoolean("visual.baseline.update")) {
            Files.createDirectories(BASELINES);
            Files.write(baseline, screenshot);
            return;
        }

        ScreenshotComparison comparison = ScreenshotComparison.of(Files.readAllBytes(baseline), screenshot);
        if (comparison.getDifferingRatio() > ALLOWED_DIFFERING_RATIO) {
            comparison.writeTo(FAILURES.resolve(fixture));
        }

        assertTrue(comparison.getDifferingRatio() <= ALLOWED_DIFFERING_RATIO,
                () -> "The %s rendering no longer looks like its baseline: %s. See %s, and record it again if the change was meant."
                        .formatted(fixture, comparison.describe(), FAILURES.resolve(fixture).toAbsolutePath()));
    }

    private byte[] screenshot(String html) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newContext(new Browser.NewContextOptions()
                    .setViewportSize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
                    .setDeviceScaleFactor(1))
                    .newPage();
            // The fixture names no remote asset, and a request leaving the container would make the
            // picture depend on the day
            page.route("**", Route::abort);

            page.setContent(readResource("visual/shell.html")
                    .replace("{{content}}", embed(html))
                    .replace("{{neutralize}}", readResource("visual/page.css")));

            return page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
        }
    }

    @SneakyThrows
    private String embed(String html) {
        return new StylesheetEmbedder().embed(html);
    }

    @SneakyThrows
    private String readResource(String path) {
        try (InputStream resource = Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream(path), "Missing resource: " + path)) {
            return new String(resource.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
