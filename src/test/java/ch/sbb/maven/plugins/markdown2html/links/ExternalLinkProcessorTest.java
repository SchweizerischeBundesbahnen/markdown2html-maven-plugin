package ch.sbb.maven.plugins.markdown2html.links;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExternalLinkProcessorTest {

    @Test
    void testExternalLinksProcessing() throws IOException {
        try (
                InputStream inputStream = RelativeLinksProcessorTest.class.getClassLoader().getResourceAsStream("links/markdown_with_html_a_tags.md");
                InputStream expectedInputStream = RelativeLinksProcessorTest.class.getClassLoader().getResourceAsStream("links/markdown_with_html_a_tags_and_target_blank.md");
        ) {
            String markdown = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String result = new ExternalLinkProcessor().processExternalLinks(markdown);
            String expectedOutput = new String(expectedInputStream.readAllBytes(), StandardCharsets.UTF_8);
            assertEquals(expectedOutput, result);
        }
    }
}
