package ch.sbb.maven.plugins.markdown2html.alert;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.commonmark.node.CustomBlock;

/**
 * A blockquote whose first line marked it as one of GitHub's alerts. It holds what was left of the quote
 * after the marker was taken off.
 */
@Getter
@RequiredArgsConstructor
public class AlertBlock extends CustomBlock {

    private final transient GitHubAlert alert;
}
