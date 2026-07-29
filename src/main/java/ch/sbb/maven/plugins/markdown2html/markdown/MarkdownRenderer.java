package ch.sbb.maven.plugins.markdown2html.markdown;

import ch.sbb.maven.plugins.markdown2html.alert.GitHubAlertsExtension;
import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.footnotes.FootnotesExtension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.task.list.items.TaskListItemsExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.select.NodeFilter;

import java.util.List;

/**
 * Renders GitHub Flavored Markdown to HTML locally, without calling the GitHub API.
 */
public class MarkdownRenderer {

    /**
     * CommonMark plus the four extensions the GFM spec adds on top of it, plus the two things GitHub renders
     * in Markdown files although the spec covers neither: footnotes and alerts.
     */
    private static final List<Extension> EXTENSIONS = List.of(
            TablesExtension.create(),
            StrikethroughExtension.create(),
            TaskListItemsExtension.create(),
            AutolinkExtension.create(),
            FootnotesExtension.create(),
            // Not in the GFM spec either, but GitHub renders them in Markdown files and the plugin used to
            // get them back from the API
            GitHubAlertsExtension.create()
    );

    private final Parser parser;
    private final HtmlRenderer renderer;

    public MarkdownRenderer() {
        this(true);
    }

    /**
     * @param highlightCode whether fenced code blocks with a known language get their syntax coloured in
     */
    public MarkdownRenderer(boolean highlightCode) {
        parser = Parser.builder()
                .extensions(EXTENSIONS)
                .build();

        HtmlRenderer.Builder builder = HtmlRenderer.builder()
                .extensions(EXTENSIONS)
                // A single newline stays a newline instead of becoming a <br>: this is how GitHub renders
                // Markdown files such as README.md. (Its /markdown API in "gfm" mode renders with hard
                // breaks instead, the way it treats issue and pull request comments - not what we want.)
                .softbreak("\n")
                // Raw HTML embedded in the Markdown is passed through, as GitHub does.
                .escapeHtml(false)
                // GitHub percent-encodes link and image destinations, so that spaces and other unsafe
                // characters in relative paths end up as valid URLs.
                .percentEncodeUrls(true);

        if (highlightCode) {
            builder.nodeRendererFactory(HighlightedCodeBlockRenderer::new);
        }

        renderer = builder.build();
    }

    public @NotNull String render(@NotNull String markdown) {
        Node document = parser.parse(markdown);
        // jsoup drops the newline the renderer puts after the last block; it is restored so that the
        // generated file ends with one, exactly as GitHub's response does.
        String html = postProcess(renderer.render(document)).stripTrailing();
        return html.isEmpty() ? html : html + "\n";
    }

    /**
     * Re-serializes the rendered markup the way GitHub does. commonmark-java writes XHTML - {@code <br />},
     * {@code <hr />}, {@code <img … />} - and escapes double quotes in text as {@code &quot;}, where GitHub
     * emits HTML5 with bare void elements and literal quotes. It also strips HTML comments, which GitHub's
     * sanitizer removes. Everything else is left byte for byte as the renderer produced it.
     */
    private static @NotNull String postProcess(@NotNull String html) {
        Document document = Jsoup.parseBodyFragment(html);
        // Pretty-printing would re-indent and re-wrap the markup, which corrupts whitespace-sensitive
        // content such as <pre> code blocks.
        document.outputSettings().prettyPrint(false);
        document.body().filter((NodeFilter) (node, depth) ->
                node instanceof Comment ? NodeFilter.FilterResult.REMOVE : NodeFilter.FilterResult.CONTINUE);
        return document.body().html();
    }
}
