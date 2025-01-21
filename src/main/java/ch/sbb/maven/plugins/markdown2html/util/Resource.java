package ch.sbb.maven.plugins.markdown2html.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Resource {
    private byte[] content;
    private String mimeType;
}
