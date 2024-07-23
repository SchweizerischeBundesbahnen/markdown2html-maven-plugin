package ch.sbb.maven.plugins.markdown2html.markdown;

import lombok.experimental.UtilityClass;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class MarkdownProcessor {

    public static String removeChapter(@NotNull String markdown, @Nullable List<String> excludeChapters) {
        if (excludeChapters == null) {
            return markdown;
        }

        for (String excludeChapter : excludeChapters) {
            markdown = removeChapter(markdown, excludeChapter);
        }

        return markdown;
    }

    public static String removeChapter(@NotNull String markdown, @Nullable String chapterTitle) {
        if (StringUtils.isEmpty(chapterTitle)) {
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
        return result.replaceAll("(?m)^\\s*\n", "\n").trim();
    }
}
