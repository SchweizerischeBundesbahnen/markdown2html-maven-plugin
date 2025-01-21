package ch.sbb.maven.plugins.markdown2html.markdown;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to fetch image: " + connection.getResponseMessage());
        }

        try (InputStream inputStream = connection.getInputStream()) {
            return new Resource(inputStream.readAllBytes(), connection.getContentType());
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

    @Getter
    @AllArgsConstructor
    public static class Resource {
        private byte[] content;
        private String mimeType;
    }
}
