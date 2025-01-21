package ch.sbb.maven.plugins.markdown2html.links;

import ch.sbb.maven.plugins.markdown2html.util.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class RelativeLinksProcessor {

    public @NotNull String processRelativeLinks(@NotNull String markdown, @NotNull String relativeLinkPrefix) {
        String markdownLinkPattern = "(?<!!)\\[(?<text>[^]]*)]\\((?<url>[^)]+)\\)";
        return Pattern.compile(markdownLinkPattern)
                .matcher(markdown)
                .replaceAll(match -> "[%s](%s)".formatted(match.group(1), Utils.replaceRelativeUrl(match.group(2), relativeLinkPrefix)));
    }
}
