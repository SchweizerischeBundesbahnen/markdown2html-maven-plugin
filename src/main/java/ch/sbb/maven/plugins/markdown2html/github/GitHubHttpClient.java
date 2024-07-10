package ch.sbb.maven.plugins.markdown2html.github;

import ch.sbb.maven.plugins.markdown2html.github.model.MarkdownRequest;
import lombok.AllArgsConstructor;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;

@AllArgsConstructor
public class GitHubHttpClient {
    private static final String GITHUB_API_URL = "https://api.github.com/markdown";
    private static final String ACCEPT_HEADER = "application/vnd.github+json";
    private static final String API_VERSION_HEADER = "X-GitHub-Api-Version";
    private static final String API_VERSION = "2022-11-28";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String GFM = "gfm";

    private final String githubToken;

    public String convertMarkdownToHtml(String markdown) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(GITHUB_API_URL);
            httpPost.setHeader(HttpHeaders.ACCEPT, ACCEPT_HEADER);
            httpPost.setHeader(API_VERSION_HEADER, API_VERSION);

            if (githubToken != null && !githubToken.isEmpty()) {
                httpPost.setHeader(AUTHORIZATION_HEADER, "Bearer " + githubToken);
            }

            MarkdownRequest markdownRequest = new MarkdownRequest(GFM, markdown);
            httpPost.setEntity(new StringEntity(markdownRequest.toJSON()));

            return executeRequest(httpClient, httpPost);
        }
    }

    private String executeRequest(CloseableHttpClient httpClient, HttpPost httpPost) throws IOException {
        HttpClientResponseHandler<String> responseHandler = response -> {
            int statusCode = response.getCode();
            if (statusCode == HttpStatus.SC_OK) {
                return EntityUtils.toString(response.getEntity());
            } else {
                throw new IllegalStateException("HTTP request failed with status: " + statusCode);
            }
        };

        return httpClient.execute(httpPost, responseHandler);
    }
}