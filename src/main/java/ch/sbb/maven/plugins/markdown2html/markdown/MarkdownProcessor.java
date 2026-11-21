package ch.sbb.maven.plugins.markdown2html.markdown;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Takes parts out of a document before it is rendered. A README carries sections that belong to whoever
 * builds the project - how to compile it, how to install it, its changelog - and not to whoever reads the
 * page it ends up on.
 * <p>
 * Everything here leaves the document untouched when given nothing to remove.
 */
@Slf4j
public class MarkdownProcessor {
    /** Creates a processor. It keeps nothing between the documents it is given. */
    public MarkdownProcessor() {
        // Nothing to set up
    }


    /**
     * @param markdown        the document to cut
     * @param excludeChapters the headings to remove, each with its own text, as in {@code ## Build}
     * @return the document without those chapters
     */
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

    /**
     * Removes one chapter: its heading and everything under it, up to the next heading of the same level or
     * higher.
     *
     * @param markdown     the document to cut
     * @param chapterTitle the heading to remove, with its own text, as in {@code ## Build}
     * @return the document without that chapter
     */
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

    /**
     * @param markdown       the document to cut
     * @param lineSubstrings the texts to look for; a line holding any of them goes
     * @return the document without those lines
     */
    public @NotNull String removeLinesContainingSubstrings(@NotNull String markdown, @Nullable List<String> lineSubstrings) {
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

    /**
     * @param markdown   the document to cut
     * @param linePrefix the text to look for; a line holding it goes, wherever in the line it sits
     * @return the document without those lines
     */
    public @NotNull String removeLinesWithSubstring(@NotNull String markdown, @Nullable String linePrefix) {
        if (linePrefix == null || linePrefix.isEmpty()) {
            return markdown;
        }

        return markdown.replaceAll("(?m)^.*" + Pattern.quote(linePrefix) + ".*(\\R|)", "");
    }

    /**
     * @param markdown     the document to cut
     * @param linePatterns the regular expressions to match; what any of them matches goes
     * @return the document without what they matched
     */
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

    /**
     * @param markdown    the document to cut
     * @param linePattern the regular expression to match; what it matches goes
     * @return the document without what it matched
     */
    public @NotNull String removeLinesWithPattern(@NotNull String markdown, @Nullable String linePattern) {
        if (linePattern == null) {
            return markdown;
        }

        return markdown.replaceAll(linePattern, "");
    }
}
