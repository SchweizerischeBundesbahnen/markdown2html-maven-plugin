package ch.sbb.maven.plugins.markdown2html.github;

import ch.sbb.maven.plugins.markdown2html.markdown.MarkdownRenderer;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Holds both sides of the comparison still: what GitHub renders the fixtures as, and what this plugin
 * renders them as, each recorded in a file next to the fixture.
 * <p>
 * {@link GitHubParityTest} answers whether the two agree today. These answer the other question - which of
 * the two moved. A failing {@code github} case means GitHub changed its output; a failing {@code local} case
 * means this plugin did. Either way {@code git diff} on the recorded file says exactly what, in text, which
 * is what a screenshot cannot do.
 * <p>
 * To accept a change, run with {@code -Dbaseline.update=true} and review the diff before committing it.
 */
class BaselineTest {

    private static final Path BASELINES = Path.of("src", "test", "resources", "baseline");
    private static final boolean UPDATE = Boolean.getBoolean("baseline.update");

    @ParameterizedTest
    @ValueSource(strings = {"strict", "full", "alerts"})
    void render_fixture_stillRendersLikeTheRecordedLocalBaseline(String fixture) {
        String rendered = new MarkdownRenderer().render(readFixture(fixture));

        assertMatchesBaseline("local-" + fixture + ".html", rendered,
                "The local renderer changed. Review the diff, then accept it with -Dbaseline.update=true.");
    }

    /**
     * The mode has to be the one each fixture was recorded in: {@code gfm} renders alerts and nothing else
     * does, and it hard-wraps every newline, which the other two fixtures are full of.
     */
    @ParameterizedTest
    @ValueSource(strings = {"strict", "full", "alerts"})
    void render_fixture_stillComesBackFromGitHubAsRecorded(String fixture) {
        String mode = "alerts".equals(fixture) ? GitHubMarkdownApi.GFM : GitHubMarkdownApi.MARKDOWN;
        String rendered = GitHubMarkdownApi.render(readFixture(fixture), mode);

        assertMatchesBaseline("github-" + fixture + ".html", rendered,
                "GitHub changed what it renders. Review the diff, then accept it with -Dbaseline.update=true.");
    }

    @SneakyThrows
    private void assertMatchesBaseline(String name, String actual, String hint) {
        Path baseline = BASELINES.resolve(name);
        if (UPDATE) {
            Files.createDirectories(BASELINES);
            Files.writeString(baseline, actual, StandardCharsets.UTF_8);
            return;
        }
        assertEquals(Files.readString(baseline, StandardCharsets.UTF_8), actual, hint);
    }

    @SneakyThrows
    private String readFixture(String fixture) {
        String path = "parity/" + fixture + ".md";
        try (InputStream inputStream = Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream(path), "Missing fixture: " + path)) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
