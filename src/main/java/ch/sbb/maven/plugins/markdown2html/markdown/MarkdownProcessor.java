package ch.sbb.maven.plugins.markdown2html.markdown;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class MarkdownProcessor {

    public @NotNull String removeChapter(@NotNull String markdown, @Nullable List<String> excludeChapters) {
        if (excludeChapters == null) {
            return markdown;
        }

        for (String excludeChapter : excludeChapters) {
            log.debug("Removing chapter: {}", excludeChapter);
            markdown = removeChapter(markdown, excludeChapter);
            log.debug("Chapter removed: {}", excludeChapter);
        }

        return markdown;
    }

    public @NotNull String removeChapter(@NotNull String markdown, @Nullable String chapterTitle) {
        if (chapterTitle == null || chapterTitle.isEmpty()) {
            return markdown;
        }

        String escapedChapterTitle = Pattern.quote(chapterTitle);
        // Determine the heading level by counting the number of '#' characters
        int headingLevel = chapterTitle.indexOf(' ') - chapterTitle.indexOf('#');

        // Match the specified chapter and its content
        String regex = "(?s)^" + escapedChapterTitle + ".*?(?=\n^#{1," + headingLevel + "}\\s|\\Z)";

        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(markdown);

        // Remove the matched chapter and its content
        String result = matcher.replaceAll("");

        // Remove leading and trailing empty lines around the remaining text
        return result.replaceAll("(?m)^\\s*\\R", "\n").trim();
    }

    public @NotNull String removeLinesContainedSubstrings(@NotNull String markdown, @Nullable List<String> lineSubstrings) {
        if (lineSubstrings == null) {
            return markdown;
        }

        for (String lineSubstring : lineSubstrings) {
            log.debug("Removing lines with string: {}", lineSubstring);
            markdown = removeLinesWithSubstring(markdown, lineSubstring);
            log.debug("Lines removed with string: {}", lineSubstring);
        }

        return markdown;
    }

    public @NotNull String removeLinesWithSubstring(@NotNull String markdown, @Nullable String linePrefix) {
        if (linePrefix == null || linePrefix.isEmpty()) {
            return markdown;
        }

        return markdown.replaceAll("(?m)^.*" + Pattern.quote(linePrefix) + ".*(\\R|)", "");
    }

    public @NotNull String removeLinesUsingRegExPatterns(@NotNull String markdown, @Nullable List<String> linePatterns) {
        if (linePatterns == null) {
            return markdown;
        }

        for (String linePattern : linePatterns) {
            log.debug("Removing lines with pattern: {}", linePattern);
            markdown = removeLinesWithPattern(markdown, linePattern);
            log.debug("Lines removed with pattern: {}", linePattern);
        }

        return markdown;
    }

    public @NotNull String removeLinesWithPattern(@NotNull String markdown, @Nullable String linePattern) {
        if (linePattern == null) {
            return markdown;
        }

        return markdown.replaceAll(linePattern, "");
    }
}
