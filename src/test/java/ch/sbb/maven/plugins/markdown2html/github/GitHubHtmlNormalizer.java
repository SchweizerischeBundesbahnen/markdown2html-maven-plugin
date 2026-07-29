package ch.sbb.maven.plugins.markdown2html.github;

import lombok.experimental.UtilityClass;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Strips the markup that github.com adds around the actual Markdown rendering, so that the local renderer
 * and the GitHub API can be compared on the parts that carry meaning.
 * <p>
 * Every rule below removes something a local renderer cannot and should not reproduce: permalink anchors,
 * Linguist syntax highlighting, the camo image proxy, anti-spam attributes. It is applied to both sides,
 * so a rule can only ever hide a difference in the constructs it names - a divergence anywhere else still
 * fails the test.
 */
@UtilityClass
class GitHubHtmlNormalizer {

    private static final String USER_CONTENT_PREFIX = "user-content-";

    String normalize(String html) {
        Document document = Jsoup.parseBodyFragment(html);
        // Pretty-printing would re-indent the markup and destroy the whitespace inside <pre>
        document.outputSettings().prettyPrint(false);
        Element body = document.body();

        unwrapHeadings(body);
        unwrapCodeBlocks(body);
        unwrapProxiedImages(body);
        dropSiteAttributes(body);
        trimTableCells(body);
        normalizeValuelessAttributes(body);

        return body.html();
    }

    /**
     * GitHub wraps every heading in {@code <div class="markdown-heading">} and appends a permalink anchor
     * with an {@code id}; the plugin's own {@code generateHeadingIds} option is what adds ids locally.
     */
    private void unwrapHeadings(Element body) {
        for (Element wrapper : body.select("div.markdown-heading")) {
            Element heading = wrapper.selectFirst("h1, h2, h3, h4, h5, h6");
            if (heading != null) {
                heading.removeAttr("class");
                heading.remove();
                wrapper.replaceWith(heading);
            }
        }
    }

    /**
     * A fenced block with a language comes back from GitHub as
     * {@code <div class="highlight highlight-source-java"><pre>} with every token wrapped in a
     * {@code <span class="pl-…">} by Linguist, against the local {@code <pre><code class="language-java">}.
     * Both are reduced to a bare {@code <pre><code>} holding the code as text, which still compares the one
     * thing that matters here: that the code itself survived rendering unchanged.
     */
    private void unwrapCodeBlocks(Element body) {
        for (Element pre : body.select("pre")) {
            Element code = pre.selectFirst("code");
            String text = code != null ? code.wholeText() : pre.wholeText();
            // GitHub's highlighted blocks have no trailing newline, the plain ones do
            if (text.endsWith("\n")) {
                text = text.substring(0, text.length() - 1);
            }

            Element normalized = new Element("pre");
            normalized.appendChild(new Element("code").text(text));

            Element parent = pre.parent();
            Element replaced = parent != null && parent.hasClass("highlight") ? parent : pre;
            replaced.replaceWith(normalized);
        }
    }

    /**
     * Images are served through GitHub's camo proxy (original URL kept in {@code data-canonical-src}), get a
     * {@code style} that fits them to the page, and unless they already are a link, are wrapped in one
     * pointing at themselves.
     */
    private void unwrapProxiedImages(Element body) {
        for (Element link : body.select("a[target=_blank][rel='noopener noreferrer']")) {
            if (link.childNodeSize() == 1 && !link.select("> img").isEmpty()) {
                link.unwrap();
            }
        }
        for (Element image : body.select("img")) {
            image.removeAttr("style");
            String canonicalSrc = image.attr("data-canonical-src");
            if (!canonicalSrc.isEmpty()) {
                image.attr("src", canonicalSrc);
                image.removeAttr("data-canonical-src");
            }
        }
    }

    /**
     * Attributes and classes that only make sense inside a GitHub page: {@code rel=nofollow} on external
     * links, the {@code user-content-} id namespace, and the class names the two footnote implementations
     * happen to disagree on.
     */
    private void dropSiteAttributes(Element body) {
        for (Element link : body.select("a[rel=nofollow]")) {
            link.removeAttr("rel");
        }
        for (Element element : body.select("[id^=" + USER_CONTENT_PREFIX + "]")) {
            element.attr("id", element.attr("id").substring(USER_CONTENT_PREFIX.length()));
        }
        for (Element link : body.select("a[href^=#" + USER_CONTENT_PREFIX + "]")) {
            link.attr("href", "#" + link.attr("href").substring(USER_CONTENT_PREFIX.length() + 1));
        }
        for (Element element : body.select("sup.footnote-ref, a.footnote-backref, section.footnotes")) {
            element.removeAttr("class");
        }
        for (Element element : body.select("[data-footnote-backref-idx]")) {
            element.removeAttr("data-footnote-backref-idx");
        }
    }

    /**
     * GitHub's HTML pipeline re-serializes table cells and leaves a newline before {@code </td>} whenever
     * the cell ends with an inline element.
     */
    private void trimTableCells(Element body) {
        for (Element cell : body.select("td, th")) {
            cell.html(cell.html().trim());
        }
    }

    /**
     * A flag attribute that carries no value is written as {@code data-footnote-ref} by one renderer and as
     * {@code data-footnote-ref=""} by the other. The two forms are the same attribute, so both are reduced
     * to the valueless one.
     */
    private void normalizeValuelessAttributes(Element body) {
        for (Element element : body.getAllElements()) {
            for (Attribute attribute : element.attributes().asList()) {
                if (attribute.getValue().isEmpty()) {
                    element.removeAttr(attribute.getKey());
                    element.attr(attribute.getKey(), true);
                }
            }
        }
    }
}
