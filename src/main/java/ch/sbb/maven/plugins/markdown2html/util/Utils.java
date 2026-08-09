package ch.sbb.maven.plugins.markdown2html.util;

import lombok.SneakyThrows;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;

/**
 * The odds and ends the processors share: telling an address that leaves the repository from one that stays
 * inside it, and reading what either of them points at.
 */
public final class Utils {

    private Utils() {
        // Nothing to instantiate; lombok's @UtilityClass would hide the constructor from javadoc, which
        // then reports a public default one this class does not have
    }

    /**
     * @param url    the address to look at
     * @param prefix what to put in front of it, with or without a trailing slash
     * @return the address made absolute, or the address itself when it already was, or when it points
     * within the same page
     */
    public static String replaceRelativeUrl(String url, String prefix) {
        // Check if the URL is neither absolute (starts with http/https) nor internal (starts with #)
        if (!isAbsoluteUrl(url) && !url.startsWith("#")) {
            // Ensure relativeLinkPrefix ends with /
            if (!prefix.endsWith("/")) {
                prefix = prefix + "/";
            }

            // Handle cases where url starts with / or not
            if (url.startsWith("/")) {
                url = prefix + url.substring(1);
            } else {
                url = prefix + url;
            }
        }
        return url;
    }

    /**
     * @param url the address to look at
     * @return whether it names a host of its own rather than a place in the repository
     */
    public static boolean isAbsoluteUrl(String url) {
        return url.matches("^(http|https|ftp)://.*");
    }

    /**
     * @param url what to fetch
     * @return what came back, with the content type the server gave it
     * @throws IllegalStateException if the server answered with anything but 200
     */
    @SneakyThrows
    public static Resource getResourceByURL(String url) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            HttpClientResponseHandler<Resource> responseHandler = response -> {
                int statusCode = response.getCode();
                if (statusCode == HttpStatus.SC_OK) {
                    return new Resource(EntityUtils.toByteArray(response.getEntity()), response.getEntity().getContentType());
                } else {
                    throw new IllegalStateException("HTTP request failed with status: " + statusCode + ", reason: " + response.getReasonPhrase());
                }
            };
            return client.execute(request, responseHandler);
        }
    }

    /**
     * @param path the file to read
     * @return its bytes, with the content type guessed from it
     * @throws java.io.FileNotFoundException if there is no such file
     */
    @SneakyThrows
    public static Resource getResourceByPath(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException("File not found: " + path);
        }
        return new Resource(Files.readAllBytes(file.toPath()), Files.probeContentType(file.toPath()));
    }
}
