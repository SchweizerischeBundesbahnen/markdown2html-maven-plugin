package ch.sbb.maven.plugins.markdown2html.images;

import ch.sbb.maven.plugins.markdown2html.TestCommons;
import ch.sbb.maven.plugins.markdown2html.util.Resource;
import ch.sbb.maven.plugins.markdown2html.util.Utils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

class ImagesProcessorTest {

    @Test
    @SuppressWarnings("java:S2699")
    void testRelativeUrlsProcessing() {
        TestCommons.runFunctionTestUsingFiles("images/markdown_images.md", "images/markdown_images_result.md",
                markdown -> new ImagesProcessor().processRelativeUrls(markdown, "http://localhost:8080"));
    }

    @Test
    @SuppressWarnings("java:S2699")
    void testEmbeddingImages() {
        try (MockedStatic<Utils> mockScopeUtils = mockStatic(Utils.class)) {
            mockScopeUtils.when(() -> Utils.isAbsoluteUrl(anyString())).thenCallRealMethod();
            mockScopeUtils.when(() -> Utils.getResourceByURL("http://localhost/images/img1.png")).thenReturn(new Resource("content1".getBytes(StandardCharsets.UTF_8), "image/png"));
            mockScopeUtils.when(() -> Utils.getResourceByPath("../resources/img2.jpg")).thenReturn(new Resource("content2".getBytes(StandardCharsets.UTF_8), "image/jpeg"));

            TestCommons.runFunctionTestUsingFiles("images/html_images_embed.md", "images/html_images_embed_result.md",
                    markdown -> new ImagesProcessor().embedImages(markdown));
        }
    }

}
