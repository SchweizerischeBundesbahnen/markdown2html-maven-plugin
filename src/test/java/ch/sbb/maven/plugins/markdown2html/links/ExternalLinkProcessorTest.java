package ch.sbb.maven.plugins.markdown2html.links;

import ch.sbb.maven.plugins.markdown2html.TestCommons;
import org.junit.jupiter.api.Test;

class ExternalLinkProcessorTest {

    @Test
    void testExternalLinksProcessing() {
        TestCommons.runFunctionTestUsingFiles("links/markdown_with_html_a_tags.md", "links/markdown_with_html_a_tags_and_target_blank.md",
                markdown -> new ExternalLinkProcessor().processExternalLinks(markdown));
    }

}
