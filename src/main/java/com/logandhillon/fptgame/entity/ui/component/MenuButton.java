package com.logandhillon.fptgame.entity.ui.component;

import com.logandhillon.fptgame.resource.Colors;
import com.logandhillon.fptgame.resource.Sounds;
import com.logandhillon.logangamelib.engine.GameMeta;
import com.logandhillon.logangamelib.entity.ui.DynamicButtonEntity;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * A stylized version of the {@link DynamicButtonEntity} made for menus.
 *
 * @author Logan Dhillon, Jack Ross
 */
public class MenuButton extends DynamicButtonEntity {
    private static final Style DEFAULT_STYLE = new Style(
            Colors.FOREGROUND, Colors.BUTTON_NORMAL, Variant.SOLID, true,
            Font.font(GameMeta.get().defaultFont.load(), FontWeight.MEDIUM, 20));
    private static final Style ACTIVE_STYLE  = new Style(
            Colors.FOREGROUND, Colors.BUTTON_HOVER, Variant.SOLID, true,
            Font.font(GameMeta.get().defaultFont.load(), FontWeight.MEDIUM, 21));

    private final Runnable pressHandler;

    private final Image icon;
    private final float ix;
    private final float iy;
    private final float iw;
    private final float ih;

    private MenuButton(String label, Image icon, float x, float y, float w, float h, float ix, float iy, float iw,
                       float ih, Runnable onPress) {
        super(label, x, y, w, h, e -> {
                  Sounds.playSfx(Sounds.UI_CLICK);
                  onPress.run();
              },
              DEFAULT_STYLE, ACTIVE_STYLE);
        this.pressHandler = () -> {
            Sounds.playSfx(Sounds.UI_CLICK);
            onPress.run();
        };
        this.icon = icon;
        this.ix = ix;
        this.iy = iy;
        this.iw = iw;
        this.ih = ih;
    }

    /**
     * Creates a new dynamic button entity using the preset styles for menu buttons.
     *
     * @param label   the text to show on the button
     * @param w       width
     * @param h       height
     * @param onPress the action that should happen when this button is clicked
     */
    public MenuButton(String label, float x, float y, float w, float h, Runnable onPress) {
        this(label.toUpperCase(), null, x, y, w, h, -1, -1, -1, -1, onPress);
    }

    /**
     * Creates a new dynamic button entity with an icon instead of text
     *
     * @param icon the text to show on the button
     */
    public MenuButton(Image icon, float x, float y, float w, float h, float ix, float iy, float iw, float ih,
                      Runnable onPress) {
        this(null, icon, x, y, w, h, ix, iy, iw, ih, onPress);
    }

    /**
     * Runs the {@code onPress} press handler.
     */
    public void onPress() {
        pressHandler.run();
    }

    @Override
    protected void onRender(GraphicsContext g, float x, float y) {
        super.onRender(g, x, y);
        // assuming the fill and font have already been set from super#onRender

        if (icon != null) {
            g.setImageSmoothing(false);
            g.drawImage(icon, ix, iy, iw, ih);
        }
        if (this.isActive()) {
            // left arrow
            g.setTextAlign(TextAlignment.LEFT);
            g.fillText(">", x + 16, y + h / 2);

            // right arrow
            g.setTextAlign(TextAlignment.RIGHT);
            g.fillText("<", x + w - 16, y + h / 2);
        }
    }
}
