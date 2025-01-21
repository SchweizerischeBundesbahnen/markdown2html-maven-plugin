package ch.sbb.maven.plugins.markdown2html.links;

import ch.sbb.maven.plugins.markdown2html.TestCommons;
import org.junit.jupiter.api.Test;

class RelativeLinksProcessorTest {

    @Test
    @SuppressWarnings("java:S2699")
    void testRelativeLinksProcessing() {
        TestCommons.runFunctionTestUsingFiles("links/markdown_links.md", "links/markdown_links_result.md",
                markdown -> new RelativeLinksProcessor().processRelativeLinks(markdown, "http://localhost:8080"));
    }

}
