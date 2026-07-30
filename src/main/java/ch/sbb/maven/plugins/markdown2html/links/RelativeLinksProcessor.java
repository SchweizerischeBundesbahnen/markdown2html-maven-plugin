package ch.sbb.maven.plugins.markdown2html.links;

import ch.sbb.maven.plugins.markdown2html.util.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * Puts a prefix in front of the links that point inside the repository. Read anywhere else than on the
 * repository page, a relative link leads nowhere, so it is turned into an absolute one.
 * <p>
 * Images are left alone here - {@link ch.sbb.maven.plugins.markdown2html.images.ImagesProcessor} handles
 * those, since they may be embedded instead.
 */
public class RelativeLinksProcessor {
    /** Creates a processor. It keeps nothing between the documents it is given. */
    public RelativeLinksProcessor() {
        // Nothing to set up
    }


    /**
     * @param markdown           the document to rewrite
     * @param relativeLinkPrefix what to put in front of a relative destination; a trailing slash is added
     *                           if it has none
     * @return the document, with every relative link made absolute
     */
    public @NotNull String processRelativeLinks(@NotNull String markdown, @NotNull String relativeLinkPrefix) {
        String markdownLinkPattern = "(?<!!)\\[(?<text>[^]]*)]\\((?<url>[^)]+)\\)";
        return Pattern.compile(markdownLinkPattern)
                .matcher(markdown)
                .replaceAll(match -> "[%s](%s)".formatted(match.group(1), Utils.replaceRelativeUrl(match.group(2), relativeLinkPrefix)));
    }
}
