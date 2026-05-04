package com.logandhillon.logangamelib.gfx;

import com.logandhillon.logangamelib.resource.ImageResource;
import com.logandhillon.logangamelib.resource.TextResource;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A texture atlas (commonly known as a tile-set) is an array of multiple textures stitched together in one large
 * image.
 * <p>
 * This class is responsible for managing said atlas, reading properties from disk, and drawing individual textures from
 * the atlas.
 *
 * @author Logan Dhillon
 */
public class TextureAtlas {
    private static final Logger LOG = LoggerContext.getContext().getLogger(TextureAtlas.class);

    /**
     * Stores all loaded textures in runtime, so they can be accessed via the {@link TextureAtlas#path} at {@code O(1)}
     * time.
     */
    private static final Map<String, TextureAtlas> LOADED_TEXTURES = new HashMap<>();

    protected final Image    image;
    protected final Metadata meta;
    protected final String   path;

    /**
     * Creates a new texture atlas, loading the {@code gfx/{image.png}} and the {@code gfx/{image.png}.atlas} files.
     *
     * @param path the name of the image (and atlas file) in the gfx folder.
     */
    private TextureAtlas(String path) {
        LOG.debug("Computing new texture atlas for '{}'", path);
        try (var img = new ImageResource(path);
             var meta = new TextResource("gfx/" + path + ".atlas")
        ) {
            this.image = img.load();
            this.meta = Metadata.fromString(meta.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.path = path;
    }

    /**
     * Tries to load the correct {@link TextureAtlas} from runtime memory, or creates a new one if it has not been
     * created already.
     *
     * @param path the name of the image (and atlas file) in the gfx folder.
     *
     * @return loaded/created {@link TextureAtlas} pointer
     */
    public static TextureAtlas load(String path) {
        return LOADED_TEXTURES.computeIfAbsent(path, TextureAtlas::new);
    }

    /**
     * Internal method for drawing a static image to the screen
     *
     * @param g     javafx graphics context from the engine's canvas
     * @param image static image to draw
     * @param row   row of image in atlas
     * @param col   col of image in atlas
     * @param x     x-pos to draw at
     * @param y     y-pos to draw at
     * @param w     width of image that will be drawn
     * @param h     height of image that will be drawn
     */
    private void draw(GraphicsContext g, Image image, int row, int col, float x, float y, float w, float h) {
        if (row < 0 || row > meta.rows || col < 0 || col > meta.cols)
            throw new IllegalArgumentException("row/col must be within 0 and the number of rows/cols in this atlas");

        g.setImageSmoothing(false);
        g.drawImage(image, row * meta.cellWidth, col * meta.cellHeight, meta.cellWidth, meta.cellHeight, x, y, w, h);
    }

    /**
     * Draws this image to the screen
     *
     * @param g   javafx graphics context from the engine's canvas
     * @param row row of image in atlas
     * @param col col of image in atlas
     * @param x   x-pos to draw at
     * @param y   y-pos to draw at
     * @param w   width of image that will be drawn
     * @param h   height of image that will be drawn
     */
    public void draw(GraphicsContext g, int row, int col, float x, float y, float w, float h) {
        draw(g, image, row, col, x, y, w, h);
    }

    /**
     * Recolors this image then draws it to the screen
     *
     * @param g     javafx graphics context from the engine's canvas
     * @param row   row of image in atlas
     * @param col   col of image in atlas
     * @param x     x-pos to draw at
     * @param y     y-pos to draw at
     * @param w     width of image that will be drawn
     * @param h     height of image that will be drawn
     * @param color color to recolor the image with
     *
     * @see ImageResource#recolor(Image, Color)
     */
    public void draw(GraphicsContext g, int row, int col, float x, float y, float w, float h, Color color) {
        draw(g, ImageResource.recolor(image, color), row, col, x, y, w, h);
    }

    /**
     * Metadata for a texture atlas
     *
     * @param cellWidth  width of each cell
     * @param cellHeight height of each cell
     * @param rows       number of rows in atlas
     * @param cols       number of columns in atlas
     */
    public record Metadata(int cellWidth, int cellHeight, int rows, int cols) {
        /**
         * Parses an input string (e.g. {@code w,h,r,c}) and loads it as a valid {@link Metadata} object.
         *
         * @param s the input string
         *
         * @return parsed metadata object
         *
         * @throws IllegalArgumentException if the input string is invalid and cannot be parsed
         */
        public static Metadata fromString(String s) throws IllegalArgumentException {
            String[] parts = s.split(",");
            if (parts.length < 4) throw new IllegalArgumentException(
                    "invalid metadata, expected 4 comma-separated parts (cellWidth,cellHeight,rows,cols)");
            try {
                return new Metadata(
                        Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3]));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "invalid metadata, expected 4 comma-separated parts (cellWidth,cellHeight,rows,cols) that can" +
                        " be parsed as integers");
            }
        }
    }

    /**
     * Gets the pathname for this {@link TextureAtlas}, which can be used to serialize & retrieve atlases from data.
     *
     * @return pathname, string
     */
    public String getPath() {
        return path;
    }
}
