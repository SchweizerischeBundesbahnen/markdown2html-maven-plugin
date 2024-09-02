package ch.sbb.maven.plugins.markdown2html.html;

import com.ibm.icu.text.Transliterator;
import org.jetbrains.annotations.NotNull;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlProcessor {

    private final Set<String> usedHeadingIds = new HashSet<>();

    public @NotNull String addHeadingIds(@NotNull String html) {
        Pattern pattern = Pattern.compile("<(?<tag>h[1-6])>(?<text>.*?)</\\k<tag>>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);

        StringBuilder modifiedHtml = new StringBuilder();

        while (matcher.find()) {
            String tag = matcher.group("tag");
            String text = matcher.group("text");
            String id = generateId(text);
            if (id.isEmpty()) {
                id = "heading-" + System.currentTimeMillis();
            }
            id = ensureUniqueId(id);
            String replacement = String.format("<%s id=\"%s\">%s</%s>", tag, id, text, tag);
            matcher.appendReplacement(modifiedHtml, replacement);
        }
        matcher.appendTail(modifiedHtml);

        return modifiedHtml.toString();
    }

    private @NotNull String generateId(@NotNull String text) {
        Transliterator transliterator = Transliterator.getInstance("Any-Latin; Latin-ASCII");
        String transliterated = transliterator.transliterate(text);
        String normalized = Normalizer.normalize(transliterated, Normalizer.Form.NFD);

        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .replaceAll("[^\\w\\s-]", "")
                .replaceAll("[-\\s]+", "-")
                .replaceAll("(^-)|(-$)", "");
    }

    private @NotNull String ensureUniqueId(@NotNull String id) {
        String uniqueId = id;
        int counter = 2;
        while (usedHeadingIds.contains(uniqueId)) {
            uniqueId = id + "-" + counter;
            counter++;
        }
        usedHeadingIds.add(uniqueId);
        return uniqueId;
    }
}