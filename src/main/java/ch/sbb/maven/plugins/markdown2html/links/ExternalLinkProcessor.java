package ch.sbb.maven.plugins.markdown2html.links;

import ch.sbb.maven.plugins.markdown2html.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class ExternalLinkProcessor {

    public @NotNull String processExternalLinks(@NotNull String html) {
        // Parse the HTML string into a Jsoup Document without altering the original formatting
        Document document = Jsoup.parse(html, "", Parser.xmlParser());

        Elements links = document.select("a[href]");

        for (Element link : links) {
            String url = link.attr("href");

            // Check if the URL is absolute
            if (Utils.isAbsoluteUrl(url)) {
                addTargetAttribute(link);
            }
        }

        // Return the modified HTML as a string while preserving the original formatting
        return document.outerHtml();
    }

    private static void addTargetAttribute(@NotNull Element link) {
        // Check if the tag already has a target attribute
        if (!link.hasAttr("target")) {
            link.attr("target", "_blank");
        }
    }
}
