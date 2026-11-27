package ch.sbb.maven.plugins.markdown2html.alert;

import lombok.Getter;
import org.commonmark.node.CustomBlock;
import org.jetbrains.annotations.NotNull;

/**
 * A blockquote whose first line marked it as one of GitHub's alerts. It holds what was left of the quote
 * after the marker was taken off.
 */
@Getter
public class AlertBlock extends CustomBlock {

    private final GitHubAlert alert;

    /**
     * @param alert the kind of alert the marker named
     */
    public AlertBlock(@NotNull GitHubAlert alert) {
        this.alert = alert;
    }
}
