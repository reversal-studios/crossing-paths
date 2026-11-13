package com.logandhillon.logangamelib.javafx;

import com.logandhillon.logangamelib.api.render.*;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.ArcType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * Implementation of the LGL {@link Renderer} API for JavaFX.
 *
 * @author Logan Dhillon
 * @see Renderer LGL Renderer API
 * @see GraphicsContext JavaFX Graphics Context
 */
public final class FXRenderer implements Renderer {
    private final GraphicsContext graphics;
    private       Color           fill      = Color.BLACK;
    private       Color           stroke    = Color.BLACK;
    private       float           lineWidth = 1;
    private       Font            font      = new Font("System", 12);
    private       TextStyle       textStyle = new TextStyle(
            Color.BLACK, TextStyle.Justification.LEFT, TextStyle.VAlign.BASELINE, Float.MAX_VALUE);

    public FXRenderer(GraphicsContext graphics) {
        this.graphics = graphics;
    }

    @Override
    public void clear(Color color) {
        graphics.setFill(toFxColor(color));
        graphics.fillRect(0, 0, graphics.getCanvas().getWidth(), graphics.getCanvas().getHeight());
    }

    @Override
    public void draw(Texture texture, float x, float y, float width, float height) {
        draw(texture, x, y, width, height, Color.WHITE);
    }

    @Override
    public void draw(Texture texture, float x, float y, float width, float height, Color tint) {
        draw(texture, new Texture.Region(0, 0, texture.width(), texture.height()), x, y, width, height, tint);
    }

    @Override
    public void draw(Texture texture, Texture.Region source, float x, float y, float width, float height, Color tint) {
        FXTexture fxTexture = requireTexture(texture);
        Image image = tint == Color.WHITE ? fxTexture.image() : fxTexture.recolor(toFxColor(tint));

        graphics.setGlobalAlpha(tint.alpha());
        graphics.setImageSmoothing(false);
        graphics.drawImage(image, source.x(), source.y(), source.width(), source.height(), x, y, width, height);
        graphics.setGlobalAlpha(1);
    }

    @Override
    public void fill(Shape shape, Color color) {
        graphics.setFill(toFxColor(color));

        switch (shape) {
            case Shape.Rect s -> graphics.fillRect(s.x(), s.y(), s.width(), s.height());
            case Shape.RoundRect s -> graphics.fillRoundRect(
                    s.x(), s.y(), s.width(), s.height(), s.radius() * 2, s.radius() * 2);
            case Shape.Circle s -> graphics.fillOval(
                    s.x() - s.radius(), s.y() - s.radius(), s.radius() * 2, s.radius() * 2);
            case Shape.Arc s -> graphics.fillArc(
                    s.x(), s.y(), s.width(), s.height(), s.azimuth(), s.bearing(), ArcType.ROUND);
            case Shape.Polygon s -> graphics.fillPolygon(
                    toDouble(s.xPoints()), toDouble(s.yPoints()), s.nPoints());
            case null, default -> throw new IllegalArgumentException("Cannot fill shape: " + shape);
        }
    }

    @Override
    public void stroke(Shape shape, Color color, float lineWidth) {
        graphics.setStroke(toFxColor(color));
        graphics.setLineWidth(lineWidth);

        switch (shape) {
            case Shape.Rect s -> graphics.strokeRect(s.x(), s.y(), s.width(), s.height());
            case Shape.RoundRect s -> graphics.strokeRoundRect(
                    s.x(), s.y(), s.width(), s.height(), s.radius() * 2, s.radius() * 2);
            case Shape.Line s -> graphics.strokeLine(s.x1(), s.y1(), s.x2(), s.y2());
            case Shape.Circle s -> graphics.strokeOval(
                    s.x() - s.radius(), s.y() - s.radius(), s.radius() * 2, s.radius() * 2);
            case Shape.Arc s -> graphics.strokeArc(
                    s.x(), s.y(), s.width(), s.height(), s.azimuth(), s.bearing(), ArcType.ROUND);
            case Shape.Polygon s -> graphics.strokePolygon(toDouble(s.xPoints()), toDouble(s.yPoints()), s.nPoints());
            case null, default -> throw new IllegalArgumentException("Cannot stroke shape: " + shape);
        }
    }

    @Override
    public void text(Font font, String text, float x, float y, TextStyle style) {
        graphics.setFont(javafx.scene.text.Font.font(font.family(), toFxWeight(font.weight()), font.size()));
        graphics.setFill(toFxColor(style.color()));
        graphics.setTextAlign(switch (style.justification()) {
            case LEFT -> TextAlignment.LEFT;
            case CENTER -> TextAlignment.CENTER;
            case RIGHT -> TextAlignment.RIGHT;
        });
        graphics.setTextBaseline(switch (style.vAlign()) {
            case TOP -> VPos.TOP;
            case CENTER -> VPos.CENTER;
            case BASELINE -> VPos.BASELINE;
            case BOTTOM -> VPos.BOTTOM;
        });
        if (style.maxWidth() == Float.MAX_VALUE) graphics.fillText(text, x, y);
        else graphics.fillText(text, x, y, style.maxWidth());
    }

    @Override
    public void clip(Shape clip, Runnable content) {
        graphics.save();
        graphics.beginPath();
        switch (clip) {
            case Shape.Rect s -> graphics.rect(s.x(), s.y(), s.width(), s.height());
            case Shape.Arc s -> graphics.arc(s.x(), s.y(), s.width(), s.height(), s.azimuth(), s.bearing());
            case Shape.Circle s -> graphics.arc(s.x(), s.y(), s.radius(), s.radius(), 0, 360);
            case null, default -> throw new IllegalArgumentException("unsupported clipping shape: " + clip);
        }
        graphics.clip();
        try {
            content.run();
        } finally {
            graphics.restore();
        }
    }

    private static FXTexture requireTexture(Texture texture) {
        if (texture instanceof FXTexture fxTexture) return fxTexture;
        throw new IllegalArgumentException("Texture does not belong to the JavaFX renderer");
    }

    private static javafx.scene.paint.Color toFxColor(Color color) {
        return new javafx.scene.paint.Color(color.red(), color.green(), color.blue(), color.alpha());
    }

    private static FontWeight toFxWeight(Font.FontWeight weight) {
        return switch (weight) {
            case NORMAL -> FontWeight.NORMAL;
            case MEDIUM -> FontWeight.MEDIUM;
            case BOLD -> FontWeight.BOLD;
        };
    }

    @Override
    public void setFill(Color color) {
        fill = color;
    }

    @Override
    public void setStroke(Color color) {
        stroke = color;
    }

    @Override
    public void setLineWidth(float width) {
        lineWidth = width;
    }

    @Override
    public void setFont(Font value) {
        font = value;
    }

    @Override
    public void setTextStyle(TextStyle value) {
        textStyle = value;
    }

    @Override
    public void fillRoundRect(float x, float y, float width, float height, float arcWidth, float arcHeight) {
        graphics.setFill(toFxColor(fill));
        graphics.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public void strokeLine(float x1, float y1, float x2, float y2) {
        graphics.setStroke(toFxColor(stroke));
        graphics.setLineWidth(lineWidth);
        graphics.strokeLine(x1, y1, x2, y2);
    }

    @Override
    public void fillText(String value, float x, float y) {
        text(font, value, x, y, textStyle);
    }

    private static double[] toDouble(float[] values) {
        double[] result = new double[values.length];
        for (int i = 0; i < values.length; i++)
            result[i] = values[i];
        return result;
    }
}
