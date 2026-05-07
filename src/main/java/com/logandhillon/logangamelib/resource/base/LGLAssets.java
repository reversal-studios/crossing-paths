package com.logandhillon.logangamelib.resource.base;

import com.logandhillon.logangamelib.resource.ImageResource;
import javafx.scene.image.Image;

import java.io.IOException;

/**
 * Contains engine-level assets for logangamelib.
 *
 * @author Logan Dhillon
 */
public class LGLAssets {
    public static final Image LGL_ICON;

    static {
        try (var res = new ImageResource("lgl.png")) {
            LGL_ICON = res.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
