package ch.sbb.maven.plugins.markdown2html.markdown;

import ch.sbb.maven.plugins.markdown2html.highlight.CodeHighlighter;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.Node;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Renders a fenced code block the way commonmark-java does - {@code <pre><code class="language-java">} - but
 * with the code itself run through the {@link CodeHighlighter} first.
 * <p>
 * Indented code blocks keep the default rendering: they carry no language, so there is nothing to highlight.
 */
class HighlightedCodeBlockRenderer implements NodeRenderer {

    private final HtmlNodeRendererContext context;
    private final HtmlWriter html;
    private final CodeHighlighter highlighter = new CodeHighlighter();

    HighlightedCodeBlockRenderer(@NotNull HtmlNodeRendererContext context) {
        this.context = context;
        this.html = context.getWriter();
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        return Set.of(FencedCodeBlock.class);
    }

    @Override
    public void render(Node node) {
        FencedCodeBlock codeBlock = (FencedCodeBlock) node;
        String language = languageOf(codeBlock.getInfo());

        Map<String, String> codeAttributes = new LinkedHashMap<>();
        if (language != null) {
            codeAttributes.put("class", "language-" + language);
        }

        html.line();
        html.tag("pre", context.extendAttributes(node, "pre", Map.of()));
        html.tag("code", context.extendAttributes(node, "code", codeAttributes));
        html.raw(highlighter.highlight(codeBlock.getLiteral(), language));
        html.tag("/code");
        html.tag("/pre");
        html.line();
    }

    /**
     * The info string may carry more than the language - GitHub reads attributes after it - so only the
     * first word counts, which is also how commonmark-java derives the {@code language-} class.
     */
    private static @Nullable String languageOf(@Nullable String info) {
        if (info == null || info.isEmpty()) {
            return null;
        }
        int space = info.indexOf(' ');
        return space == -1 ? info : info.substring(0, space);
    }
}
