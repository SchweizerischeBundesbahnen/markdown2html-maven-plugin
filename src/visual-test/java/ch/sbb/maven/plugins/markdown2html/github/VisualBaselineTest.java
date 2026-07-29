package ch.sbb.maven.plugins.markdown2html.github;

import ch.sbb.maven.plugins.markdown2html.html.StylesheetEmbedder;
import ch.sbb.maven.plugins.markdown2html.markdown.MarkdownRenderer;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

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
 * image pinned to the same version as the dependency. Outside it the test aborts rather than fail on
 * somebody's font stack. To take the picture again after an intended change:
 * <pre>
 * ./src/visual-test/record-baselines.sh
 * </pre>
 */
class VisualBaselineTest {

    private static final String FIXTURE = "visual/baseline.md";
    private static final Path BASELINE = Path.of("src", "visual-test", "resources", "baseline", "baseline.png");
    private static final Path FAILURE = Path.of("target", "visual-baseline");

    private static final int VIEWPORT_WIDTH = 1000;
    private static final int VIEWPORT_HEIGHT = 800;

    /**
     * In one and the same container the rendering is reproducible to the pixel, so anything at all is a
     * change worth looking at.
     */
    private static final double ALLOWED_DIFFERING_RATIO = 0.0;

    @Test
    @SneakyThrows
    void render_fixture_stillLooksLikeTheRecordedBaseline() {
        if (!"1".equals(System.getenv("VISUAL_BASELINE_CONTAINER"))) {
            abort("The image baselines only hold in the pinned Playwright container - "
                    + "run ./src/visual-test/record-baselines.sh, or --check to compare against them");
        }

        byte[] screenshot = screenshot(new MarkdownRenderer().render(readResource(FIXTURE)));

        if (Boolean.getBoolean("visual.baseline.update")) {
            Files.createDirectories(BASELINE.getParent());
            Files.write(BASELINE, screenshot);
            return;
        }

        ScreenshotComparison comparison = ScreenshotComparison.of(Files.readAllBytes(BASELINE), screenshot);
        if (comparison.getDifferingRatio() > ALLOWED_DIFFERING_RATIO) {
            comparison.writeTo(FAILURE);
        }

        assertTrue(comparison.getDifferingRatio() <= ALLOWED_DIFFERING_RATIO,
                () -> "The rendering no longer looks like the baseline: %s. See %s, and record it again if the change was meant."
                        .formatted(comparison.describe(), FAILURE.toAbsolutePath()));
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
            page.route("**", route -> route.abort());

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
