package ch.sbb.maven.plugins.markdown2html;

import ch.sbb.maven.plugins.markdown2html.github.GitHubHttpClient;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

@Mojo(name = "convert", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class MarkdownToHtmlMojo extends AbstractMojo {

    @Parameter(property = "inputFile", defaultValue = "${project.basedir}/README.md")
    private File inputFile;

    @Parameter(property = "outputFile", defaultValue = "${project.basedir}/README.html")
    private File outputFile;

    @Parameter(property = "tokenEnvVarName", defaultValue = "GITHUB_TOKEN")
    private String tokenEnvVarName;

    public void execute() throws MojoExecutionException {
        String githubToken = System.getenv(tokenEnvVarName);
        GitHubHttpClient gitHubHttpClient = new GitHubHttpClient(githubToken);

        try {
            String markdown = Files.readString(inputFile.toPath());

            String html = gitHubHttpClient.convertMarkdownToHtml(markdown);

            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(html);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Error processing markdown file", e);
        }
    }
}