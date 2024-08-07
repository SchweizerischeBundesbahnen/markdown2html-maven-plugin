package ch.sbb.maven.plugins.markdown2html;

import ch.sbb.maven.plugins.markdown2html.github.GitHubHttpClient;
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
import java.nio.file.StandardOpenOption;
import java.util.List;

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

    @Parameter(property = "excludeChapters")
    private List<String> excludeChapters;

    public void execute() throws MojoExecutionException {
        try {
            log.info("processing markdown file: {}", inputFile);

            String githubToken = System.getenv(tokenEnvVarName);
            GitHubHttpClient gitHubHttpClient = new GitHubHttpClient(githubToken);

            String markdown = Files.readString(inputFile.toPath(), StandardCharsets.UTF_8);

            String filteredMarkdown = MarkdownProcessor.removeChapter(markdown, excludeChapters);

            String html = gitHubHttpClient.convertMarkdownToHtml(filteredMarkdown);

            log.info("writing html to file: {}", outputFile);

            Files.writeString(outputFile.toPath(), html, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

            log.info("markdown to html successfully converted");
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