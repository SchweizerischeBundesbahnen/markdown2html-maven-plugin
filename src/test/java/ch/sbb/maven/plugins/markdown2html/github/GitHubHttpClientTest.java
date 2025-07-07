package ch.sbb.maven.plugins.markdown2html.github;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GitHubHttpClientTest {

    public static Stream<Arguments> convertMarkdownToHtml_validMarkdown_returnsHtml_parameters() {
        return Stream.of(
                Arguments.of("", ""),
                Arguments.of("Hello, **world**!", "<p>Hello, <strong>world</strong>!</p>"),
                Arguments.of("# Façade", "<h1>Façade</h1>")
        );
    }

    @ParameterizedTest
    @MethodSource("convertMarkdownToHtml_validMarkdown_returnsHtml_parameters")
    void convertMarkdownToHtml_validMarkdown_returnsHtml(String inputMarkdown, String expectedHtml) throws Exception {
        String githubToken = System.getenv("GITHUB_TOKEN");
        String result = new GitHubHttpClient(githubToken)
                .convertMarkdownToHtml(inputMarkdown);

        assertEquals(expectedHtml, result);
    }
}
