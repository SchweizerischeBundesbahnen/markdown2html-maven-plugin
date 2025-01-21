package ch.sbb.maven.plugins.markdown2html.markdown;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedConstruction;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UtilsTest {

    @TempDir
    Path tempDir;

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

    @Test
    void testIsAbsoluteUrl() {
        assertTrue(Utils.isAbsoluteUrl("http://example.com"));
        assertTrue(Utils.isAbsoluteUrl("https://example.com/path"));
        assertTrue(Utils.isAbsoluteUrl("ftp://example.com/file.txt"));
        assertFalse(Utils.isAbsoluteUrl("file://example.com"));
        assertFalse(Utils.isAbsoluteUrl("/path/to/file"));
        assertFalse(Utils.isAbsoluteUrl("relative/path"));
        assertFalse(Utils.isAbsoluteUrl(""));
    }

    @Test
    void testGetResourceByURL() throws IOException {
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        String testUrl = "https://example.com/image.jpg";
        byte[] testContent = "test content".getBytes();
        String contentType = "image/jpeg";

        try (MockedConstruction<URL> mocked = mockConstruction(URL.class,
                (mock, context) -> {
                    when(mock.openConnection()).thenReturn(mockConnection);
                })) {

            when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
            when(mockConnection.getContentType()).thenReturn(contentType);
            when(mockConnection.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(testContent));

            Utils.Resource resource = Utils.getResourceByURL(testUrl);

            assertNotNull(resource);
            assertArrayEquals(testContent, resource.getContent());
            assertEquals(contentType, resource.getMimeType());

            verify(mockConnection).setRequestMethod("GET");
            verify(mockConnection).connect();
        }
    }

    @Test
    void testGetResourceByURLThrowsError() throws IOException {
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        String testUrl = "https://example.com/notfound.jpg";

        try (MockedConstruction<URL> mocked = mockConstruction(URL.class,
                (mock, context) -> {
                    when(mock.openConnection()).thenReturn(mockConnection);
                })) {

            when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_NOT_FOUND);
            when(mockConnection.getResponseMessage()).thenReturn("Not Found");

            IOException exception = assertThrows(IOException.class,
                    () -> Utils.getResourceByURL(testUrl));
            assertTrue(exception.getMessage().contains("Failed to fetch image"));
        }
    }

    @Test
    void testGetResourceByPath() throws IOException {
        String testContent = "test content";
        Path testFile = tempDir.resolve("test.txt");
        Files.write(testFile, testContent.getBytes());

        Utils.Resource resource = Utils.getResourceByPath(testFile.toString());

        assertNotNull(resource);
        assertEquals(testContent, new String(resource.getContent()));
        assertNotNull(resource.getMimeType());
    }

    @Test
    void testGetResourceByPathThrowsError() {
        String nonExistentPath = tempDir.resolve("nonexistent.txt").toString();

        assertThrows(java.io.FileNotFoundException.class,
                () -> Utils.getResourceByPath(nonExistentPath));
    }

    @Test
    void testResource() {
        byte[] content = "test".getBytes();
        String mimeType = "text/plain";

        Utils.Resource resource = new Utils.Resource(content, mimeType);

        assertArrayEquals(content, resource.getContent());
        assertEquals(mimeType, resource.getMimeType());
    }
}
