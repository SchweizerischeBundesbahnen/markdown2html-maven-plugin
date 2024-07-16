package ch.sbb.maven.plugins.markdown2html;

import ch.sbb.maven.plugins.markdown2html.github.GitHubHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;

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

    public void execute() throws MojoExecutionException {
        try {
            log.info("processing markdown file: {}", inputFile);

            String githubToken = System.getenv(tokenEnvVarName);
            GitHubHttpClient gitHubHttpClient = new GitHubHttpClient(githubToken);

            String markdown = Files.readString(inputFile.toPath());

            String html = gitHubHttpClient.convertMarkdownToHtml(markdown);

            log.info("writing html to file: {}", outputFile);
            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(html);
            }

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