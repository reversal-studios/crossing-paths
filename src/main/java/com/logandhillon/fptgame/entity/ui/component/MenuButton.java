package com.logandhillon.fptgame.entity.ui.component;

import com.logandhillon.fptgame.resource.Colors;
import com.logandhillon.fptgame.resource.Sounds;
import com.logandhillon.logangamelib.api.render.Font;
import com.logandhillon.logangamelib.api.render.Renderer;
import com.logandhillon.logangamelib.api.render.TextStyle;
import com.logandhillon.logangamelib.api.render.Texture;
import com.logandhillon.logangamelib.engine.GameMeta;
import com.logandhillon.logangamelib.entity.ui.DynamicButtonEntity;

/**
 * A stylized version of the {@link DynamicButtonEntity} made for menus.
 *
 * @author Logan Dhillon, Jack Ross
 */
public class MenuButton extends DynamicButtonEntity {
    private static final Style DEFAULT_STYLE = new Style(
            Colors.FOREGROUND, Colors.BUTTON_NORMAL, Variant.SOLID, true,
            new Font(GameMeta.get().defaultFont.load(), 20, Font.FontWeight.MEDIUM));
    private static final Style ACTIVE_STYLE  = new Style(
            Colors.FOREGROUND, Colors.BUTTON_HOVER, Variant.SOLID, true,
            new Font(GameMeta.get().defaultFont.load(), 21, Font.FontWeight.MEDIUM));

    private final Runnable pressHandler;

    private final Texture icon;
    private final float   ix;
    private final float   iy;
    private final float   iw;
    private final float   ih;

    private MenuButton(String label, Texture icon, float x, float y, float w, float h, float ix, float iy, float iw,
                       float ih, Runnable onPress) {
        super(
                label, x, y, w, h, e -> {
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
    public MenuButton(Texture icon, float x, float y, float w, float h, float ix, float iy, float iw, float ih,
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
    protected void onRender(Renderer r, float x, float y) {
        super.onRender(r, x, y);

        if (icon != null) r.draw(icon, ix, iy, iw, ih);

        if (this.isActive()) {
            // left arrow
            r.text(
                    style.font(), ">", x + 16, y + h / 2,
                    new TextStyle(style.labelColor(), TextStyle.Justification.LEFT, TextStyle.VAlign.CENTER, w));

            // right arrow
            r.text(
                    style.font(), "<", x + w - 16, y + h / 2,
                    new TextStyle(style.labelColor(), TextStyle.Justification.RIGHT, TextStyle.VAlign.CENTER, w));
        }
    }
}
