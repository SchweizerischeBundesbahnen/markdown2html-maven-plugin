package ch.sbb.maven.plugins.markdown2html.images;

import ch.sbb.maven.plugins.markdown2html.markdown.Utils;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;
import java.util.Objects;
import java.util.regex.Pattern;

public class ImagesProcessor {

    private static final String DEFAULT_IMAGE_MIME_TYPE = "image/*";

    public @NotNull String processRelativeUrls(@NotNull String markdown, @NotNull String relativeLinkPrefix) {
        String markdownLinkPattern = "!\\[(?<text>[^]]*)]\\((?<url>[^)]+)\\)";
        return Pattern.compile(markdownLinkPattern)
                .matcher(markdown)
                .replaceAll(match -> "![%s](%s)".formatted(match.group(1), Utils.replaceRelativeUrl(match.group(2), relativeLinkPrefix)));
    }

    public @NotNull String embedImages(@NotNull String html) {
        String imgSrcPattern = "<img[^<>]*src=([\"'])(?<url>[^(\"|')]*)([\"'])";
        return Pattern.compile(imgSrcPattern)
                .matcher(html)
                .replaceAll(match -> match.group(0).replace(match.group(2), loadAndGetAsBase64Content(match.group(2))));
    }

    @SneakyThrows
    private String loadAndGetAsBase64Content(String urlOrPath) {
        Utils.Resource resource = Utils.isAbsoluteUrl(urlOrPath) ? Utils.getResourceByURL(urlOrPath) : Utils.getResourceByPath(urlOrPath);
        String base64Content = Base64.getEncoder().encodeToString(resource.getContent());
        return "data:%s;base64,%s".formatted(Objects.toString(resource.getMimeType(), DEFAULT_IMAGE_MIME_TYPE), base64Content);
    }
}
