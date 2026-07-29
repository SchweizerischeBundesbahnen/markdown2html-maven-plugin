package ch.sbb.maven.plugins.markdown2html.links;

import ch.sbb.maven.plugins.markdown2html.TestCommons;
import org.junit.jupiter.api.Test;

class ExternalLinkProcessorTest {

    @Test
    @SuppressWarnings("java:S2699")
    void testExternalLinksProcessing() {
        TestCommons.runFunctionTestUsingFiles("links/markdown_with_html_a_tags.md", "links/markdown_with_html_a_tags_and_target_blank.md",
                markdown -> new ExternalLinkProcessor().processExternalLinks(markdown));
    }

    /**
     * Void elements must survive untouched. Parsed as XML, a &lt;br&gt; is merely unclosed, so the rest
     * of the block became its child and every one of them was closed at the end of it - emitting stray
     * &lt;/br&gt; tags that a browser reads as further line breaks, one blank line each. Whitespace has
     * to survive too: this step only adds a target attribute, it does not reformat the document.
     */
    @Test
    @SuppressWarnings("java:S2699")
    void testVoidElementsAndWhitespaceAreLeftIntact() {
        TestCommons.runFunctionTestUsingFiles("links/html_with_void_elements.md", "links/html_with_void_elements_result.md",
                html -> new ExternalLinkProcessor().processExternalLinks(html));
    }

}
