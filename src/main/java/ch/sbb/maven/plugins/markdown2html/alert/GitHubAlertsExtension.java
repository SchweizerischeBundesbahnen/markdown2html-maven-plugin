package ch.sbb.maven.plugins.markdown2html.alert;

import org.commonmark.Extension;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.Node;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.parser.PostProcessor;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.html.HtmlWriter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Renders GitHub's alerts: a blockquote whose first line is {@code [!NOTE]}, {@code [!TIP]},
 * {@code [!IMPORTANT]}, {@code [!WARNING]} or {@code [!CAUTION]} becomes the callout github.com shows
 * instead of a quote.
 * <p>
 * They are not part of the GFM spec and commonmark-java does not know them, but GitHub renders them in
 * Markdown files, and so did this plugin while it called the GitHub API. The markup is GitHub's own, down to
 * the octicon, so that {@code embedStylesheet} styles it.
 */
public class GitHubAlertsExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {

    /**
     * @return the extension, to hand to the parser and the renderer alike
     */    /** Creates the extension. Prefer {@link #create()}, which is what commonmark-java expects. */
    public GitHubAlertsExtension() {
        // Nothing to set up
    }

    /**
     * @return the extension, to hand to the parser and the renderer alike
     */
    public static Extension create() {
        return new GitHubAlertsExtension();
    }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.postProcessor(new AlertPostProcessor());
    }

    @Override
    public void extend(HtmlRenderer.Builder rendererBuilder) {
        rendererBuilder.nodeRendererFactory(AlertNodeRenderer::new);
    }

    /**
     * Replaces every marked blockquote with an {@link AlertBlock} holding what is left of it once the marker
     * line is gone.
     */
    private static class AlertPostProcessor implements PostProcessor {

        @Override
        public Node process(Node document) {
            List<BlockQuote> quotes = new ArrayList<>();
            document.accept(new AbstractVisitor() {
                @Override
                public void visit(BlockQuote blockQuote) {
                    quotes.add(blockQuote);
                    visitChildren(blockQuote);
                }
            });

            // Collected first: converting rewires the tree the visitor is walking
            quotes.forEach(AlertPostProcessor::convert);
            return document;
        }

        private static void convert(@NotNull BlockQuote blockQuote) {
            if (!(blockQuote.getFirstChild() instanceof Paragraph paragraph)
                    || !(paragraph.getFirstChild() instanceof Text marker)) {
                return;
            }
            GitHubAlert alert = GitHubAlert.ofMarker(marker.getLiteral());
            // The marker has to be the whole first line, as it is on github.com
            if (alert == null || !(marker.getNext() == null || marker.getNext() instanceof SoftLineBreak)) {
                return;
            }

            removeMarker(paragraph, marker);

            AlertBlock alertBlock = new AlertBlock(alert);
            // Read the sibling before moving the node: appending it rewires the link this walk follows
            Node child = blockQuote.getFirstChild();
            while (child != null) {
                Node next = child.getNext();
                alertBlock.appendChild(child);
                child = next;
            }
            blockQuote.insertBefore(alertBlock);
            blockQuote.unlink();
        }

        /**
         * Takes the marker and the line break after it off the first paragraph, and the paragraph itself if
         * nothing else was on it.
         */
        private static void removeMarker(@NotNull Paragraph paragraph, @NotNull Text marker) {
            Node lineBreak = marker.getNext();
            marker.unlink();
            if (lineBreak != null) {
                lineBreak.unlink();
            }
            if (paragraph.getFirstChild() == null) {
                paragraph.unlink();
            }
        }
    }

    private static class AlertNodeRenderer implements NodeRenderer {

        private final HtmlNodeRendererContext context;
        private final HtmlWriter html;

        AlertNodeRenderer(@NotNull HtmlNodeRendererContext context) {
            this.context = context;
            this.html = context.getWriter();
        }

        @Override
        public Set<Class<? extends Node>> getNodeTypes() {
            return Set.of(AlertBlock.class);
        }

        @Override
        public void render(Node node) {
            GitHubAlert alert = ((AlertBlock) node).getAlert();

            html.line();
            html.raw("<div class=\"markdown-alert " + alert.getCssClass() + "\">");
            html.line();
            html.raw("<p class=\"markdown-alert-title\">" + alert.getIcon() + alert.getLabel() + "</p>");
            html.line();
            renderChildren(node);
            html.line();
            html.raw("</div>");
            html.line();
        }

        private void renderChildren(@NotNull Node parent) {
            Node child = parent.getFirstChild();
            while (child != null) {
                Node next = child.getNext();
                context.render(child);
                child = next;
            }
        }
    }
}
