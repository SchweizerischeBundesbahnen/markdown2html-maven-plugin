package ch.sbb.maven.plugins.markdown2html.images;

/**
 * What the plugin does with the images a document points at.
 */
public enum ImageProcessingType {

    /** Leaves every image where it is, so the generated file keeps pointing at it. */
    NONE,

    /** Reads each image and writes it into the file as a data URI, so it carries its own pictures. */
    EMBED
}
