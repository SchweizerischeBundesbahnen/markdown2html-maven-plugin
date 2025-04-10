package ch.sbb.maven.plugins.markdown2html;

import ch.sbb.maven.plugins.markdown2html.github.GitHubHttpClient;
import ch.sbb.maven.plugins.markdown2html.html.HtmlProcessor;
import ch.sbb.maven.plugins.markdown2html.images.ImageProcessingType;
import ch.sbb.maven.plugins.markdown2html.images.ImagesProcessor;
import ch.sbb.maven.plugins.markdown2html.links.ExternalLinkProcessor;
import ch.sbb.maven.plugins.markdown2html.links.RelativeLinksProcessor;
import ch.sbb.maven.plugins.markdown2html.markdown.MarkdownProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

@SuppressWarnings("unused")
@Slf4j
@Mojo(name = "convert", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class MarkdownToHtmlMojo extends AbstractMojo {

    @Parameter(property = "inputFile", defaultValue = "${project.basedir}/README.md")
    private File inputFile;

    @Parameter(property = "outputFile", defaultValue = "${project.basedir}/README.html")
    private File outputFile;

    @Parameter(property = "tokenEnvVarName", defaultValue = "GITHUB_TOKEN")
    private String tokenEnvVarName;

    @Parameter(property = "failOnError", defaultValue = "true")
    private boolean failOnError;

    @Parameter(property = "generateHeadingIds", defaultValue = "false")
    private boolean generateHeadingIds;

    @Parameter(property = "excludeChapters")
    private List<String> excludeChapters;

    @Parameter(property = "relativeLinkPrefix")
    private String relativeLinkPrefix;

    @Parameter(property = "openExternalLinksInNewTab", defaultValue = "false")
    private boolean openExternalLinksInNewTab;

    @Parameter(property = "removeLinesWithStrings")
    private List<String> removeLinesWithStrings;

    @Parameter(property = "removeLinesUsingPatterns")
    private List<String> removeLinesUsingPatterns;

    @Parameter(property = "imageProcessingType", defaultValue = "NONE")
    private ImageProcessingType imageProcessingType;

    public void execute() throws MojoExecutionException {
        try {
            log.info("Processing markdown file: {}", inputFile);

            String githubToken = System.getenv(tokenEnvVarName);
            GitHubHttpClient gitHubHttpClient = new GitHubHttpClient(githubToken);

            String markdown = Files.readString(inputFile.toPath(), StandardCharsets.UTF_8);

            MarkdownProcessor markdownProcessor = new MarkdownProcessor();
            String filteredMarkdown = markdownProcessor.removeChapter(markdown, excludeChapters);
            filteredMarkdown = markdownProcessor.removeLinesContainingSubstrings(filteredMarkdown, removeLinesWithStrings);
            filteredMarkdown = markdownProcessor.removeLinesUsingRegExPatterns(filteredMarkdown, removeLinesUsingPatterns);

            if (relativeLinkPrefix != null && !relativeLinkPrefix.isEmpty()) {
                log.info("Processing relative links with prefix: {}", relativeLinkPrefix);
                filteredMarkdown = new RelativeLinksProcessor().processRelativeLinks(filteredMarkdown, relativeLinkPrefix);
                if (ImageProcessingType.NONE.equals(imageProcessingType)) {
                    filteredMarkdown = new ImagesProcessor().processRelativeUrls(filteredMarkdown, relativeLinkPrefix);
                }
            }

            String html = gitHubHttpClient.convertMarkdownToHtml(filteredMarkdown);

            if (ImageProcessingType.EMBED.equals(imageProcessingType)) {
                log.info("Embedding images");
                html = new ImagesProcessor().embedImages(html);
            }

            if (openExternalLinksInNewTab) {
                log.info("Make external links opening in new tab");
                html = new ExternalLinkProcessor().processExternalLinks(html);
            }

            if (generateHeadingIds) {
                log.info("Generating heading IDs");
                html = new HtmlProcessor().addHeadingIds(html);
            }

            log.info("Writing html to file: {}", outputFile);

            Files.writeString(outputFile.toPath(), html, StandardCharsets.UTF_8);

            log.info("Markdown successfully converted to HTML");
        } catch (Exception e) {
            if (failOnError) {
                throw new MojoExecutionException("Error processing markdown file", e);
            } else {
                log.error("Error processing markdown file:", e);
                log.warn("Skipping execution due to failOnError=false");
            }
        }
    }
}
