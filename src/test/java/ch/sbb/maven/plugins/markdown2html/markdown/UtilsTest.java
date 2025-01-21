package ch.sbb.maven.plugins.markdown2html.markdown;

import ch.sbb.maven.plugins.markdown2html.util.Resource;
import ch.sbb.maven.plugins.markdown2html.util.Utils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    public void testGetResourceByURL_Success() throws Exception {
        String testUrl = "http://example.com/resource";
        byte[] responseBytes = "test data".getBytes();
        String contentType = "application/octet-stream";

        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse mockHttpResponse = mock(CloseableHttpResponse.class);

        // Simulate the HTTP response
        when(mockHttpResponse.getCode()).thenReturn(HttpStatus.SC_OK);
        when(mockHttpResponse.getEntity()).thenReturn(new org.apache.hc.core5.http.io.entity.AbstractHttpEntity(contentType, null) {
            @Override
            public void close() {

            }

            @Override
            public InputStream getContent() {
                return new ByteArrayInputStream(responseBytes);
            }

            @Override
            public boolean isStreaming() {
                return true;
            }

            @Override
            public long getContentLength() {
                return responseBytes.length;
            }
        });

        // Mock the HttpClient to execute the request
        when(mockHttpClient.execute(any(HttpGet.class), any(HttpClientResponseHandler.class))).thenAnswer(invocation -> {
            HttpClientResponseHandler<Resource> handler = invocation.getArgument(1);
            return handler.handleResponse(mockHttpResponse);
        });

        try (MockedStatic<HttpClients> mockedHttpClients = mockStatic(HttpClients.class)) {
            mockedHttpClients.when(HttpClients::createDefault).thenReturn(mockHttpClient);

            // Call the method under test
            Resource result = Utils.getResourceByURL(testUrl);

            // Assertions
            assertNotNull(result);
            assertArrayEquals(responseBytes, result.getContent());
            assertEquals(contentType, result.getMimeType());
        }
    }

    @Test
    public void testGetResourceByURL_Failure() throws Exception {
        String testUrl = "http://example.com/resource";

        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse mockHttpResponse = mock(CloseableHttpResponse.class);
        when(mockHttpResponse.getCode()).thenReturn(HttpStatus.SC_INTERNAL_SERVER_ERROR);

        // Mock the HttpClient to execute the request
        when(mockHttpClient.execute(any(HttpGet.class), any(HttpClientResponseHandler.class))).thenAnswer(invocation -> {
            HttpClientResponseHandler<Resource> handler = invocation.getArgument(1);
            return handler.handleResponse(mockHttpResponse);
        });

        // Mock the static method HttpClients.createDefault()
        try (MockedStatic<HttpClients> mockedHttpClients = mockStatic(HttpClients.class)) {
            mockedHttpClients.when(HttpClients::createDefault).thenReturn(mockHttpClient);

            // Assert that an exception is thrown
            IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
                Utils.getResourceByURL(testUrl);
            });
            assertTrue(thrown.getMessage().contains("HTTP request failed"));
        }
    }

    @Test
    void testGetResourceByPath() throws IOException {
        String testContent = "test content";
        Path testFile = tempDir.resolve("test.txt");
        Files.write(testFile, testContent.getBytes());

        Resource resource = Utils.getResourceByPath(testFile.toString());

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

}
