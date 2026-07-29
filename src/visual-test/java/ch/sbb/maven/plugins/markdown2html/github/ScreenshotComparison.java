package ch.sbb.maven.plugins.markdown2html.github;

import lombok.SneakyThrows;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Compares two screenshots pixel by pixel and, when they differ, writes both of them plus a map of where
 * they differ, so a failure in CI can be looked at instead of guessed about.
 */
@Value
class ScreenshotComparison {

    /**
     * Anti-aliasing of identical text is deterministic in the same browser, but rounding around a sub-pixel
     * boundary is not; a channel difference this small is not something a reader could see.
     */
    private static final int CHANNEL_TOLERANCE = 8;

    private static final int DIFF_COLOR = 0xffff0000;
    private static final int MATCH_COLOR = 0xffffffff;

    int width;
    int height;
    int differingPixels;
    BufferedImage expected;
    BufferedImage actual;
    BufferedImage diff;

    @SneakyThrows
    static @NotNull ScreenshotComparison of(byte @NotNull [] expectedPng, byte @NotNull [] actualPng) {
        BufferedImage expected = ImageIO.read(new ByteArrayInputStream(expectedPng));
        BufferedImage actual = ImageIO.read(new ByteArrayInputStream(actualPng));

        int width = Math.max(expected.getWidth(), actual.getWidth());
        int height = Math.max(expected.getHeight(), actual.getHeight());

        BufferedImage diff = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int differing = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean same = matches(expected, actual, x, y);
                diff.setRGB(x, y, same ? MATCH_COLOR : DIFF_COLOR);
                if (!same) {
                    differing++;
                }
            }
        }

        return new ScreenshotComparison(width, height, differing, expected, actual, diff);
    }

    /**
     * A pixel outside one of the images - the two are not the same height - counts as a difference.
     */
    private static boolean matches(@NotNull BufferedImage expected, @NotNull BufferedImage actual, int x, int y) {
        boolean insideExpected = x < expected.getWidth() && y < expected.getHeight();
        boolean insideActual = x < actual.getWidth() && y < actual.getHeight();
        if (!insideExpected || !insideActual) {
            return false;
        }

        int left = expected.getRGB(x, y);
        int right = actual.getRGB(x, y);
        for (int shift = 0; shift <= 24; shift += 8) {
            if (Math.abs(((left >> shift) & 0xff) - ((right >> shift) & 0xff)) > CHANNEL_TOLERANCE) {
                return false;
            }
        }
        return true;
    }

    double getDifferingRatio() {
        return (double) differingPixels / (width * (long) height);
    }

    @SneakyThrows
    void writeTo(@NotNull Path directory) {
        Files.createDirectories(directory);
        ImageIO.write(expected, "png", directory.resolve("github.png").toFile());
        ImageIO.write(actual, "png", directory.resolve("local.png").toFile());
        ImageIO.write(diff, "png", directory.resolve("diff.png").toFile());
    }

    @NotNull String describe() {
        return "%d of %d pixels differ (%.3f%%), %dx%d against %dx%d"
                .formatted(differingPixels, width * height, getDifferingRatio() * 100,
                        expected.getWidth(), expected.getHeight(), actual.getWidth(), actual.getHeight());
    }
}
