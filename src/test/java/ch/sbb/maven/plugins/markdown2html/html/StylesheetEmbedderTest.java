package ch.sbb.maven.plugins.markdown2html.html;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StylesheetEmbedderTest {

    @Test
    @SneakyThrows
    void embed_html_wrapsItInTheStylesheet() {
        String result = new StylesheetEmbedder().embed("<h1>Title</h1>\n");

        assertTrue(result.startsWith("<style>\n"), result.substring(0, 40));
        assertTrue(result.endsWith("</style>\n<div class=\"markdown-body\">\n<h1>Title</h1>\n</div>\n"),
                result.substring(result.length() - 80));
    }

    /**
     * Nothing in the stylesheet may reach outside the wrapper, or it would restyle the page the generated
     * file is embedded in, and nothing may be fetched over the network at display time.
     */
    @Test
    @SneakyThrows
    void embed_stylesheet_isScopedAndSelfContained() {
        String stylesheet = new StylesheetEmbedder().embed("").split("</style>")[0];

        assertEquals(0, stylesheet.lines()
                .filter(line -> line.endsWith("{"))
                .filter(line -> !line.contains(".markdown-body") && !line.startsWith("@"))
                .count(), "Every selector has to be scoped to .markdown-body");
        assertTrue(stylesheet.lines()
                .filter(line -> line.contains("url("))
                .allMatch(line -> line.contains("url(\"data:")), "Assets have to be inlined as data URIs");
    }
}
