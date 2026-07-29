package ch.sbb.maven.plugins.markdown2html.alert;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GitHubAlertTest {

    @ParameterizedTest
    @CsvSource({
            "'[!NOTE]', NOTE",
            "'[!TIP]', TIP",
            "'[!IMPORTANT]', IMPORTANT",
            "'[!WARNING]', WARNING",
            "'[!CAUTION]', CAUTION"
    })
    void ofMarker_marker_returnsItsAlert(String marker, GitHubAlert expected) {
        assertEquals(expected, GitHubAlert.ofMarker(marker));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "[!UNKNOWN]",   // no alert goes by that name
            "[!NOTE",       // not closed
            "!NOTE]",       // not opened
            "NOTE",
            "",
            "[!note]"       // github.com matches the name in upper case only
    })
    void ofMarker_anythingElse_returnsNothing(String marker) {
        assertNull(GitHubAlert.ofMarker(marker));
    }

    @ParameterizedTest
    @EnumSource(GitHubAlert.class)
    void getCssClass_alert_isTheOneTheStylesheetKnows(GitHubAlert alert) {
        assertEquals("markdown-alert-" + alert.name().toLowerCase(), alert.getCssClass());
    }

    @ParameterizedTest
    @EnumSource(GitHubAlert.class)
    void getIcon_alert_isTheOcticonGitHubPutsInTheTitle(GitHubAlert alert) {
        String icon = alert.getIcon();

        assertTrue(icon.startsWith("<svg data-component=\"Octicon\""), icon);
        assertTrue(icon.contains("octicon-" + alert.getOcticon() + " mr-2"), icon);
        assertTrue(icon.endsWith("<path d=\"" + alert.getPath() + "\"></path></svg>"), icon);
    }
}
