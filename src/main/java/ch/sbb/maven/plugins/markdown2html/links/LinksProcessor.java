package ch.sbb.maven.plugins.markdown2html.links;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinksProcessor {

    public @NotNull String processRelativeLinks(@NotNull String markdown, @NotNull String relativeLinkPrefix) {
        String markdownLinkPattern = "\\[(?<text>[^\\]]*)\\]\\((?<url>[^)]+)\\)";
        Pattern pattern = Pattern.compile(markdownLinkPattern);
        Matcher matcher = pattern.matcher(markdown);

        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            replaceLink(matcher, result, relativeLinkPrefix);
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private static void replaceLink(Matcher matcher, StringBuffer result, @NotNull String relativeLinkPrefix) {
        String linkText = matcher.group("text");
        String url = matcher.group("url");

        // Check if the URL is neither absolute (starts with http/https) nor internal (starts with #)
        if (!url.matches("^(http|https|ftp)://.*") && !url.startsWith("#")) {
            // Ensure relativeLinkPrefix ends with /
            if (!relativeLinkPrefix.endsWith("/")) {
                relativeLinkPrefix = relativeLinkPrefix + "/";
            }

            // Handle cases where url starts with / or not
            if (url.startsWith("/")) {
                url = relativeLinkPrefix + url.substring(1);
            } else {
                url = relativeLinkPrefix + url;
            }
        }

        // Replace the markdown link with the HTML <a> tag
        matcher.appendReplacement(result, "<a href=\"" + url + "\">" + linkText + "</a>");
    }

    public @NotNull String processExternalLinks(@NotNull String string) {
        String regex = "<a\\b(?<attributesbefore>(?:\\s+[^>]*?)?)(?<href>href\\s*=\\s*['\"](?<url>[^'\"]+)['\"])(?<attributesafter>(?:\\s+[^>]*?)?)>";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(string);

        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String originalTag = matcher.group(0);
            String attributesBefore = matcher.group("attributesbefore");
            String href = matcher.group("href");
            String url = matcher.group("url");
            String attributesAfter = matcher.group("attributesafter");

            // Check if the URL is absolute
            if (url.matches("^(http|https|ftp)://.*")) {
                // Check if the tag already has a target attribute
                if (!(attributesBefore.contains("target=") || attributesAfter.contains("target="))) {
                    String modifiedTag = "<a"  + (attributesBefore.isBlank() ? " " : attributesBefore) + href + " target=\"_blank\"" + (attributesAfter.isBlank() ? "" : attributesAfter) + ">";
                    matcher.appendReplacement(result, modifiedTag);
                } else {
                    matcher.appendReplacement(result, originalTag);
                }
            } else {
                matcher.appendReplacement(result, originalTag);
            }
        }

        matcher.appendTail(result);

        return result.toString();
    }
}
