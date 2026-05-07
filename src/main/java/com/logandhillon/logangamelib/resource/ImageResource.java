package com.logandhillon.logangamelib.resource;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Loads a resource from the /gfx/ folder as an {@link Image}
 *
 * @author Logan Dhillon
 */
public class ImageResource extends Resource<Image> {
    private static final Logger LOG = LoggerContext.getContext().getLogger(ImageResource.class);

    /**
     * A hash-based cache of all recoloured images, mapped to their {@link Object#hashCode()} (from the image + the
     * tint)
     */
    private static final HashMap<Integer, Image> CACHED_RECOLORS = new HashMap<>();

    /**
     * Creates a new resource and opens an {@link InputStream} for it.
     *
     * @param path the relative path to the resource
     *
     * @throws FileNotFoundException if the file doesn't exist
     */
    public ImageResource(String path) throws FileNotFoundException {
        super("gfx/" + path);
    }

    /**
     * Utility method for efficiently recolouring (and caching) an {@link Image}
     * <p>
     * If the image was previously recolored, it will be immediately pulled from cache; otherwise it will calculate on
     * the spot.
     *
     * @param src  the source image
     * @param tint the color to recolor with
     *
     * @return the recolored image
     */
    public static Image recolor(Image src, Color tint) {
        // first try getting from cache
        int hash = src.hashCode() + tint.hashCode();
        Image cache = CACHED_RECOLORS.get(hash);
        if (cache != null) return cache;

        LOG.debug("Recolored image not cached, calculating for color #{}", tint.toString().substring(2));

        int w = (int)src.getWidth();
        int h = (int)src.getHeight();
        WritableImage output = new WritableImage(w, h);

        // iterate through each pixel and recolor it
        for (int ix = 0; ix < w; ix++) {
            for (int iy = 0; iy < h; iy++) {
                Color base = src.getPixelReader().getColor(ix, iy);
                // calculate new color by base * tint; use original opacity
                output.getPixelWriter().setColor(ix, iy, new Color(
                        base.getRed() * tint.getRed(),
                        base.getGreen() * tint.getGreen(),
                        base.getBlue() * tint.getBlue(),
                        base.getOpacity()
                ));
            }
        }

        // store in cache
        CACHED_RECOLORS.put(hash, output);
        return output;
    }

    @Override
    public Image load() {
        assert this.stream != null;
        return new Image(this.stream);
    }
}
