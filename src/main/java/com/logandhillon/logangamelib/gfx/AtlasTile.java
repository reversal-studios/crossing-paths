package com.logandhillon.logangamelib.gfx;

import com.logandhillon.fptgame.networking.proto.LevelProto;
import com.logandhillon.logangamelib.networking.ProtoSerializable;
import com.logandhillon.logangamelib.resource.ImageResource;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * A {@link ProtoSerializable} representation of a tile in a {@link TextureAtlas}. Should be used to represent
 * individual tiles in a {@link TextureAtlas}, even if you do not plan on using protobufs or serialization.
 *
 * @author Logan Dhillon
 */
public class AtlasTile implements ProtoSerializable<LevelProto.AtlasTile> {
    private final TextureAtlas atlas;
    private final int          row;
    private final int          col;

    /**
     * Creates a new atlas tile
     *
     * @param atlas pointer to original texture atlas
     * @param row   row of tile in texture atlas
     * @param col   col of tile in texture atlas
     */
    public AtlasTile(TextureAtlas atlas, int row, int col) {
        this.atlas = atlas;
        this.row = row;
        this.col = col;
    }

    /**
     * Draws this image to the screen
     *
     * @param g javafx graphics context from the engine's canvas
     * @param x x-pos to draw at
     * @param y y-pos to draw at
     * @param w width of image that will be drawn
     * @param h height of image that will be drawn
     */
    public void draw(GraphicsContext g, float x, float y, float w, float h) {
        atlas.draw(g, row, col, x, y, w, h);
    }

    /**
     * Recolours this image then draws it to the screen
     *
     * @param g     javafx graphics context from the engine's canvas
     * @param x     x-pos to draw at
     * @param y     y-pos to draw at
     * @param w     width of image that will be drawn
     * @param h     height of image that will be drawn
     * @param color colour to recolour the image with
     *
     * @see ImageResource#recolor(Image, Color)
     */
    public void draw(GraphicsContext g, float x, float y, float w, float h, Color color) {
        if (color == null) this.draw(g, x, y, w, h);
        else atlas.draw(g, row, col, x, y, w, h, color);
    }

    @Override
    public LevelProto.AtlasTile serialize() {
        return LevelProto.AtlasTile.newBuilder()
                                   .setAtlasPath(atlas.getPath())
                                   .setRow(row)
                                   .setCol(col)
                                   .build();
    }

    /**
     * Loads a message into a texture atlas
     *
     * @param msg encoded atlas tile protobuf message
     *
     * @return atlas tile
     */
    public static AtlasTile load(LevelProto.AtlasTile msg) {
        return new AtlasTile(TextureAtlas.load(msg.getAtlasPath()), msg.getRow(), msg.getCol());
    }
}
