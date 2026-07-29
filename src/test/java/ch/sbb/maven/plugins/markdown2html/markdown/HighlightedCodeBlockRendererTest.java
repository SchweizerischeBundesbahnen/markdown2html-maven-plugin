package ch.sbb.maven.plugins.markdown2html.markdown;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HighlightedCodeBlockRendererTest {

    @ParameterizedTest
    @CsvSource({
            "java, java",
            // GitHub reads attributes after the language, so only the first word names it
            "'java title=\"Test.java\"', java"
    })
    void languageOf_infoString_returnsTheFirstWord(String info, String expected) {
        assertEquals(expected, HighlightedCodeBlockRenderer.languageOf(info));
    }

    /**
     * A fence with nothing after it: the parser reports an empty info string, and a node built by hand can
     * have none at all.
     */
    @Test
    void languageOf_noInfoString_returnsNull() {
        assertNull(HighlightedCodeBlockRenderer.languageOf(""));
        assertNull(HighlightedCodeBlockRenderer.languageOf(null));
    }
}
