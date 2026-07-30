package ch.sbb.maven.plugins.markdown2html.util;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

/**
 * Something read from a file or fetched over the network, together with what it is.
 */
@Getter
public class Resource {

    /** The bytes themselves. */
    private final byte[] content;

    /** What they are, as the server or the file system reported it; {@code null} when neither could tell. */
    private final String mimeType;

    /**
     * @param content  the bytes that were read
     * @param mimeType what they are, or {@code null} when whoever handed them over did not say
     */
    public Resource(byte[] content, @Nullable String mimeType) {
        this.content = content;
        this.mimeType = mimeType;
    }
}
