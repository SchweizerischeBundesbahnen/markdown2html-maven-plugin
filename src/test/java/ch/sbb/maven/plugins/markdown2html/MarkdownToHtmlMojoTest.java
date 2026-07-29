package ch.sbb.maven.plugins.markdown2html;

import ch.sbb.maven.plugins.markdown2html.images.ImageProcessingType;
import lombok.SneakyThrows;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Drives the mojo end to end over a file in a temporary directory: every parameter switches a step of the
 * pipeline on or off, and what the switches do together is not visible from the tests of the single steps.
 */
class MarkdownToHtmlMojoTest {

    @TempDir
    Path directory;

    private Path inputFile;
    private Path outputFile;
    private MarkdownToHtmlMojo mojo;

    @BeforeEach
    void setUp() {
        inputFile = directory.resolve("README.md");
        outputFile = directory.resolve("README.html");

        mojo = new MarkdownToHtmlMojo();
        set("inputFile", inputFile.toFile());
        set("outputFile", outputFile.toFile());
        set("failOnError", true);
        set("imageProcessingType", ImageProcessingType.NONE);
        set("highlightCode", true);
    }

    @Test
    void execute_markdown_writesTheRenderedHtml() {
        String html = convert("# Title\n\nA paragraph.\n");

        assertEquals("<h1>Title</h1>\n<p>A paragraph.</p>\n", html);
    }

    /**
     * The parameter does nothing but say so; what matters is that a pom still passing it keeps working.
     */
    @Test
    void execute_deprecatedTokenParameter_convertsAnyway() {
        set("tokenEnvVarName", "GITHUB_TOKEN");

        assertEquals("<p>Text.</p>\n", convert("Text.\n"));
    }

    @Test
    void execute_excludeChapters_dropsThem() {
        set("excludeChapters", List.of("## Build"));

        String html = convert("# Title\n\n## Build\n\nmvn package\n\n## Usage\n\nRun it.\n");

        assertFalse(html.contains("Build"), html);
        assertTrue(html.contains("Usage"), html);
    }

    @Test
    void execute_removeLines_dropsThem() {
        set("removeLinesWithStrings", List.of("badge"));
        set("removeLinesUsingPatterns", List.of("(?m)^.*\\[Coverage].*(\\R|)"));

        String html = convert("keep me\n\nbadge line\n\n[Coverage](https://example.com)\n");

        assertEquals("<p>keep me</p>\n", html);
    }

    @Test
    void execute_relativeLinkPrefix_rewritesLinksAndImages() {
        set("relativeLinkPrefix", "https://localhost:9090/");

        String html = convert("[doc](docs/page.md) ![pic](img/x.png)\n");

        assertTrue(html.contains("href=\"https://localhost:9090/docs/page.md\""), html);
        assertTrue(html.contains("src=\"https://localhost:9090/img/x.png\""), html);
    }

    /**
     * An empty prefix means the same as none at all - a pom configuring the parameter to nothing must not
     * end up with every relative link rewritten to itself.
     */
    @Test
    void execute_emptyRelativeLinkPrefix_leavesLinksAlone() {
        set("relativeLinkPrefix", "");

        assertEquals("<p><a href=\"docs/page.md\">doc</a></p>\n", convert("[doc](docs/page.md)\n"));
    }

    /**
     * With the images about to be embedded there is no point in prefixing their URLs first - the file they
     * name is read from disk.
     */
    @Test
    void execute_relativeLinkPrefixWithEmbeddedImages_embedsInsteadOfPrefixing() {
        Path image = writeFile("pic.png", "not really a png");
        set("relativeLinkPrefix", "https://localhost:9090/");
        set("imageProcessingType", ImageProcessingType.EMBED);

        // Concatenated rather than formatted: the newline ends a line of Markdown, it is not a line
        // separator for the platform to choose
        String html = convert("[doc](docs/page.md) ![pic](" + image.toAbsolutePath() + ")\n");

        assertTrue(html.contains("href=\"https://localhost:9090/docs/page.md\""), html);
        assertTrue(html.contains("src=\"data:"), html);
    }

    @Test
    void execute_openExternalLinksInNewTab_addsTheTarget() {
        set("openExternalLinksInNewTab", true);

        String html = convert("[out](https://example.com) [in](docs/page.md)\n");

        assertTrue(html.contains("<a href=\"https://example.com\" target=\"_blank\">out</a>"), html);
        assertTrue(html.contains("<a href=\"docs/page.md\">in</a>"), html);
    }

    @Test
    void execute_generateHeadingIds_addsThem() {
        set("generateHeadingIds", true);

        assertEquals("<h1 id=\"the-title\">The Title</h1>\n", convert("# The Title\n"));
    }

    @Test
    void execute_embedStylesheet_wrapsTheOutput() {
        set("embedStylesheet", true);

        String html = convert("# Title\n");

        assertTrue(html.startsWith("<style>\n"), html.substring(0, 20));
        assertTrue(html.endsWith("</style>\n<div class=\"markdown-body\">\n<h1>Title</h1>\n</div>\n"),
                html.substring(html.length() - 80));
    }

    @Test
    void execute_highlightCodeOff_leavesTheCodePlain() {
        set("highlightCode", false);

        assertEquals("<pre><code class=\"language-java\">int x;\n</code></pre>\n",
                convert("```java\nint x;\n```\n"));
    }

    @Test
    void execute_highlightCodeOn_coloursTheCode() {
        assertTrue(convert("```java\nint x;\n```\n").contains("<span style=\"color:#cf222e\">int</span>"));
    }

    @Test
    void execute_unreadableInputAndFailOnError_fails() {
        set("inputFile", directory.resolve("missing.md").toFile());

        assertThrows(MojoExecutionException.class, () -> mojo.execute());
    }

    @Test
    @SneakyThrows
    void execute_unreadableInputAndFailOnErrorOff_writesNothing() {
        set("inputFile", directory.resolve("missing.md").toFile());
        set("failOnError", false);

        mojo.execute();

        assertFalse(Files.exists(outputFile));
    }

    @SneakyThrows
    private String convert(String markdown) {
        Files.writeString(inputFile, markdown, StandardCharsets.UTF_8);
        mojo.execute();
        return Files.readString(outputFile, StandardCharsets.UTF_8);
    }

    @SneakyThrows
    private Path writeFile(String name, String content) {
        Path file = directory.resolve(name);
        Files.writeString(file, content, StandardCharsets.UTF_8);
        return file;
    }

    /**
     * The parameters are private fields Maven injects; a test has to do the injecting itself.
     */
    @SneakyThrows
    private void set(String parameter, Object value) {
        Field field = MarkdownToHtmlMojo.class.getDeclaredField(parameter);
        field.setAccessible(true);
        field.set(mojo, value);
    }
}
