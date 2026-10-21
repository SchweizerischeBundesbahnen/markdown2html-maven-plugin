package ch.sbb.maven.plugins.markdown2html.links;

import ch.sbb.maven.plugins.markdown2html.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Makes the links that leave the document open in a new tab, so that a reader following one does not lose
 * the page it is embedded in.
 */
public class ExternalLinkProcessor {
    /** Creates a processor. It keeps nothing between the documents it is given. */
    public ExternalLinkProcessor() {
        // Nothing to set up
    }


    /**
     * @param html the rendered document
     * @return the same markup, with {@code target="_blank"} on every absolute link that did not carry a
     * target already
     */
    public @NotNull String processExternalLinks(@NotNull String html) {
        // Parsed as an HTML body fragment, NOT as XML. To an XML parser a void element like <br> is
        // merely unclosed, so everything after it becomes its child and all of them are closed at the
        // end of the enclosing block - serializing as `text<br>more</br></br>`. A browser reads each
        // stray </br> as another line break, so a paragraph carrying three soft breaks rendered with
        // three extra blank lines after it.
        Document document = Jsoup.parseBodyFragment(html);
        // Keep the original formatting: pretty-printing re-indents and re-wraps the markup, which
        // matters for whitespace-sensitive content such as <pre> code blocks.
        document.outputSettings().prettyPrint(false);

        Elements links = document.select("a[href]");

        for (Element link : links) {
            String url = link.attr("href");

            // Check if the URL is absolute
            if (Utils.isAbsoluteUrl(url)) {
                addTargetAttribute(link);
            }
        }

        // Only the fragment that came in: parseBodyFragment wraps it in a full html/head/body document.
        return document.body().html();
    }

    private static void addTargetAttribute(@NotNull Element link) {
        // Check if the tag already has a target attribute
        if (!link.hasAttr("target")) {
            link.attr("target", "_blank");
        }
    }
}
