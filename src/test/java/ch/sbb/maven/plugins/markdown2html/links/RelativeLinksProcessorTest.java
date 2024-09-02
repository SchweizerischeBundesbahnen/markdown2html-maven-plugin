package ch.sbb.maven.plugins.markdown2html.links;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RelativeLinksProcessorTest {

    @Test
    void testRelativeLinksProcessing() throws IOException {
        try (
                InputStream inputStream = RelativeLinksProcessorTest.class.getClassLoader().getResourceAsStream("links/markdown_links.md");
                InputStream expectedInputStream = RelativeLinksProcessorTest.class.getClassLoader().getResourceAsStream("links/markdown_with_html_a_tags.md");
        ) {
            String markdown = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String result = new RelativeLinksProcessor().processRelativeLinks(markdown, "http://localhost:8080");
            String expectedOutput = new String(expectedInputStream.readAllBytes(), StandardCharsets.UTF_8);
            assertEquals(expectedOutput, result);
        }
    }

}