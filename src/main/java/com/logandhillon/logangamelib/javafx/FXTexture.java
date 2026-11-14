package com.logandhillon.logangamelib.javafx;

import com.logandhillon.logangamelib.api.render.Texture;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.util.HashMap;

/**
 * JavaFX-backed texture, kept out of the public LGL rendering API.
 *
 * @author Logan Dhillon
 * @see FXRenderer
 */
record FXTexture(Image image) implements Texture {
    private static final Logger LOG = LoggerContext.getContext().getLogger(FXTexture.class);

    /**
     * A hash-based cache of all recoloured images, mapped to their {@link Object#hashCode()} (from the image + the
     * tint)
     */
    private static final HashMap<Integer, Image> CACHED_RECOLORS = new HashMap<>();

    @Override
    public int width() {
        return (int)image.getWidth();
    }

    @Override
    public int height() {
        return (int)image.getHeight();
    }

    /**
     * Efficiently recolours and caches the {@link Image} of this texture.
     * <p>
     * If the image was previously recoloured, it will be immediately pulled from cache; otherwise it will calculate on
     * the spot.
     *
     * @param tint the colour to recolour with
     *
     * @return the recoloured image
     */
    public Image recolor(Color tint) {
        // remove alpha for more efficient cache hits
        Color color = Color.color(tint.getRed(), tint.getGreen(), tint.getBlue(), 1);

        // first try getting from cache
        int hash = this.image.hashCode() + color.hashCode();
        Image cache = CACHED_RECOLORS.get(hash);
        if (cache != null) return cache;

        LOG.debug("Recolored image not cached, calculating for color #{}", color.toString().substring(2));

        int w = (int)this.image.getWidth();
        int h = (int)this.image.getHeight();
        WritableImage output = new WritableImage(w, h);

        // iterate through each pixel and recolour it
        for (int ix = 0; ix < w; ix++) {
            for (int iy = 0; iy < h; iy++) {
                Color base = this.image.getPixelReader().getColor(ix, iy);
                // calculate new colour by base * tint; use original opacity
                output.getPixelWriter().setColor(
                        ix, iy, new Color(
                                base.getRed() * color.getRed(),
                                base.getGreen() * color.getGreen(),
                                base.getBlue() * color.getBlue(),
                                base.getOpacity()
                        ));
            }
        }

        // store in cache
        CACHED_RECOLORS.put(hash, output);
        return output;
    }
}
