package ch.sbb.maven.plugins.markdown2html.markdown;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilsTest {

    @Test
    void testReplaceRelativeUrl() {
        // Relative URL with prefix that ends with a slash
        assertEquals("https://example.com/path/to/resource",
                Utils.replaceRelativeUrl("/path/to/resource", "https://example.com/"));

        // Relative URL with prefix that does not end with a slash
        assertEquals("https://example.com/path/to/resource",
                Utils.replaceRelativeUrl("/path/to/resource", "https://example.com"));

        // Relative URL without a leading slash
        assertEquals("https://example.com/path/to/resource",
                Utils.replaceRelativeUrl("path/to/resource", "https://example.com"));

        // Absolute URL
        assertEquals("http://absolute.url/resource",
                Utils.replaceRelativeUrl("http://absolute.url/resource", "https://example.com"));

        // HTTPS Absolute URL
        assertEquals("https://absolute.url/resource",
                Utils.replaceRelativeUrl("https://absolute.url/resource", "https://example.com"));

        // Internal link (starts with #)
        assertEquals("#internal-section",
                Utils.replaceRelativeUrl("#internal-section", "https://example.com"));

        // Empty URL
        assertEquals("https://example.com/",
                Utils.replaceRelativeUrl("", "https://example.com"));

        // Empty prefix
        assertEquals("/path/to/resource",
                Utils.replaceRelativeUrl("/path/to/resource", ""));

        // Prefix with trailing slash and relative URL without leading slash
        assertEquals("https://example.com/resource",
                Utils.replaceRelativeUrl("resource", "https://example.com/"));
    }
}
