package ch.sbb.maven.plugins.markdown2html.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;

@UtilityClass
public class Utils {

    public String replaceRelativeUrl(String url, String prefix) {
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

    public boolean isAbsoluteUrl(String url) {
        return url.matches("^(http|https|ftp)://.*");
    }

    @SneakyThrows
    public Resource getResourceByURL(String url) {
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

    @SneakyThrows
    public Resource getResourceByPath(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException("File not found: " + path);
        }
        return new Resource(Files.readAllBytes(file.toPath()), Files.probeContentType(file.toPath()));
    }
}
