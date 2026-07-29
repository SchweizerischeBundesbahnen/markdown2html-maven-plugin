package ch.sbb.maven.plugins.markdown2html.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.experimental.UtilityClass;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assumptions.abort;

/**
 * Minimal client for <a href="https://docs.github.com/en/rest/markdown">GitHub's markdown API</a>, used by
 * {@link GitHubParityTest} as the reference implementation to compare the local renderer against.
 * <p>
 * The API is not part of the plugin itself anymore, so this lives in the test sources only.
 */
@UtilityClass
class GitHubMarkdownApi {

    private static final String URL = "https://api.github.com/markdown";
    private static final Timeout TIMEOUT = Timeout.ofSeconds(30);

    /**
     * Renders Markdown the way GitHub renders a file such as {@code README.md}: a single newline stays a
     * newline. What it does not render is alerts, which is what {@link #GFM} is for.
     */
    static final String MARKDOWN = "markdown";

    /**
     * Renders the way issue and pull request comments are rendered - every newline becomes a {@code <br>},
     * which a README must not do. It is the only mode that renders alerts, and the mode this plugin used
     * while it still called the API.
     */
    static final String GFM = "gfm";

    /**
     * Renders the given markdown through the GitHub API, or aborts the calling test if the API cannot be
     * reached or refuses to answer - the parity tests are a cross-check against an external service, they
     * must not turn a broken network or an exhausted rate limit into a build failure.
     */
    String render(String markdown, String mode) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(URL);
            httpPost.setConfig(RequestConfig.custom()
                    .setConnectTimeout(TIMEOUT)
                    .setResponseTimeout(TIMEOUT)
                    .build());
            httpPost.setHeader(HttpHeaders.ACCEPT, "application/vnd.github+json");
            httpPost.setHeader("X-GitHub-Api-Version", "2022-11-28");

            String token = System.getenv("GITHUB_TOKEN");
            if (token != null && !token.isEmpty()) {
                // Unauthenticated requests are limited to 60 per hour and IP, which CI runners share
                httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            }

            httpPost.setEntity(new StringEntity(requestBody(markdown, mode), ContentType.APPLICATION_JSON));

            return httpClient.execute(httpPost, response -> {
                if (response.getCode() != HttpStatus.SC_OK) {
                    return abort("GitHub markdown API answered with status %d (%s)"
                            .formatted(response.getCode(), response.getReasonPhrase()));
                }
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            });
        } catch (IOException e) {
            return abort("GitHub markdown API is not reachable: " + e);
        }
    }

    private String requestBody(String markdown, String mode) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode request = objectMapper.createObjectNode();
        request.put("mode", mode);
        request.put("text", markdown);
        return objectMapper.writeValueAsString(request);
    }
}
