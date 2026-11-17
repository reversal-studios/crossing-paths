package com.logandhillon.logangamelib.javafx;

import com.logandhillon.logangamelib.api.event.CompositeSubscription;
import com.logandhillon.logangamelib.api.event.Event;
import com.logandhillon.logangamelib.api.event.Subscription;
import com.logandhillon.logangamelib.api.input.InputHandler;
import com.logandhillon.logangamelib.api.input.KeyCode;
import com.logandhillon.logangamelib.api.input.KeyboardEvent;
import com.logandhillon.logangamelib.api.input.MouseClickEvent;
import com.logandhillon.logangamelib.math.Vec2f;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Implementation of the LGL input API for JavaFX.
 *
 * @author Logan Dhillon
 */
public final class FXInput implements InputHandler, AutoCloseable {
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    private final CopyOnWriteArrayList<Consumer<KeyboardEvent>> keyUpHandlers    =
            new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Consumer<KeyboardEvent>> keyDownHandlers  =
            new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Consumer<KeyboardEvent>> keyTypedHandlers =
            new CopyOnWriteArrayList<>();

    private final CopyOnWriteArrayList<Consumer<MouseClickEvent>> mouseDownHandlers    =
            new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Consumer<MouseClickEvent>> mouseUpHandlers      =
            new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Consumer<MouseClickEvent>> mouseClickedHandlers =
            new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Consumer<MouseClickEvent>> mouseMovedHandlers   =
            new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Consumer<MouseClickEvent>> mouseDraggedHandlers =
            new CopyOnWriteArrayList<>();

    private final EventHandler<KeyEvent> keyEventHandler =
            e -> dispatch(keyUpHandlers, new FXKeyboardEvent(e, KeyCode.of(e.getCode().getCode())));

    private final EventHandler<MouseEvent> pointerPressedHandler  =
            e -> dispatch(mouseDownHandlers, new FXMouseClickEvent(e));
    private final EventHandler<MouseEvent> pointerReleasedHandler =
            e -> dispatch(mouseUpHandlers, new FXMouseClickEvent(e));
    private final EventHandler<MouseEvent> pointerClickedHandler  =
            e -> dispatch(mouseClickedHandlers, new FXMouseClickEvent(e));
    private final EventHandler<MouseEvent> pointerMovedHandler    =
            e -> dispatch(mouseMovedHandlers, new FXMouseClickEvent(e));
    private final EventHandler<MouseEvent> pointerDraggedHandler  =
            e -> dispatch(mouseDraggedHandlers, new FXMouseClickEvent(e));

    private Scene   scene;
    private Canvas  canvas;
    private boolean closed;

    /**
     * Allows you to construct before the scene is built so game code can register input handlers early. Call
     * {@link attach} once the JavaFX Scene exist.
     */
    public FXInput() {}

    public void attach(Scene scene, Canvas canvas) {
        if (closed) throw new IllegalStateException("Input adapter is closed");
        if (this.scene != null) throw new IllegalStateException("Input adapter is already attached");

        this.scene = scene;
        this.canvas = canvas;

        scene.addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
        scene.addEventHandler(KeyEvent.KEY_RELEASED, keyEventHandler);
        scene.addEventHandler(KeyEvent.KEY_TYPED, keyEventHandler);

        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, pointerPressedHandler);
        scene.addEventHandler(MouseEvent.MOUSE_RELEASED, pointerReleasedHandler);
        scene.addEventHandler(MouseEvent.MOUSE_CLICKED, pointerClickedHandler);
        scene.addEventHandler(MouseEvent.MOUSE_MOVED, pointerMovedHandler);
        scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, pointerDraggedHandler);
    }

    @Override
    public void onKeyDown(Consumer<KeyboardEvent> handler) {
        subscribe(keyUpHandlers, handler);
    }

    @Override
    public void onKeyUp(Consumer<KeyboardEvent> handler) {
        subscribe(keyDownHandlers, handler);
    }

    @Override
    public void onKeyTyped(Consumer<KeyboardEvent> handler) {
        subscribe(keyTypedHandlers, handler);
    }

    @Override
    public void onMouseDown(Consumer<MouseClickEvent> handler) {
        subscribe(mouseDownHandlers, handler);
    }

    @Override
    public void onMouseUp(Consumer<MouseClickEvent> handler) {
        subscribe(mouseUpHandlers, handler);
    }

    @Override
    public void onMouseClicked(Consumer<MouseClickEvent> handler) {
        subscribe(mouseClickedHandlers, handler);
    }

    @Override
    public void onMouseMoved(Consumer<MouseClickEvent> handler) {
        subscribe(mouseMovedHandlers, handler);
    }

    @Override
    public void onMouseDragged(Consumer<MouseClickEvent> handler) {
        subscribe(mouseDraggedHandlers, handler);
    }

    private <T> void subscribe(CopyOnWriteArrayList<Consumer<T>> handlers, Consumer<T> handler) {
        if (closed) throw new IllegalStateException("Input adapter is closed");
        if (handler == null) throw new NullPointerException("handler");

        handlers.add(handler);
        subscriptions.add(new Subscription(() -> handlers.remove(handler)));
    }

    private <T extends Event> void dispatch(CopyOnWriteArrayList<Consumer<T>> handlers, T event) {
        for (Consumer<T> handler: handlers) {
            if (closed || event.isConsumed()) return;
            handler.accept(event);
        }
    }

    @Override
    public void close() {
        if (closed) return;
        closed = true;
        subscriptions.close();

        if (scene != null) {
            scene.removeEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
            scene.removeEventHandler(KeyEvent.KEY_RELEASED, keyEventHandler);
            scene.removeEventHandler(KeyEvent.KEY_TYPED, keyEventHandler);

            scene.removeEventHandler(MouseEvent.MOUSE_PRESSED, pointerPressedHandler);
            scene.removeEventHandler(MouseEvent.MOUSE_RELEASED, pointerReleasedHandler);
            scene.removeEventHandler(MouseEvent.MOUSE_CLICKED, pointerClickedHandler);
            scene.removeEventHandler(MouseEvent.MOUSE_MOVED, pointerMovedHandler);
            scene.removeEventHandler(MouseEvent.MOUSE_DRAGGED, pointerDraggedHandler);
        }

        scene = null;
        canvas = null;

        keyUpHandlers.clear();
        keyDownHandlers.clear();
        keyTypedHandlers.clear();
        mouseDownHandlers.clear();
        mouseUpHandlers.clear();
        mouseClickedHandlers.clear();
        mouseMovedHandlers.clear();
        mouseDraggedHandlers.clear();
    }

    /**
     * Gets a record of {@link KeyCode.Modifiers} from a JavaFX input event, either {@link KeyEvent} or
     * {@link MouseEvent}.
     *
     * @param event JavaFX input event as documented above
     *
     * @return record of keyboard modifiers
     *
     * @throws IllegalArgumentException if JavaFX input type is not a key or mouse event.
     */
    private static KeyCode.Modifiers modifiers(InputEvent event) {
        if (event instanceof KeyEvent keyEvent) {
            return new KeyCode.Modifiers(
                    keyEvent.isShiftDown(),
                    keyEvent.isControlDown(),
                    keyEvent.isAltDown(),
                    keyEvent.isMetaDown());
        } else if (event instanceof MouseEvent mouseEvent) {
            return new KeyCode.Modifiers(
                    mouseEvent.isShiftDown(),
                    mouseEvent.isControlDown(),
                    mouseEvent.isAltDown(),
                    mouseEvent.isMetaDown());
        }
        throw new IllegalArgumentException("JavaFX event must be of type KeyEvent or MouseEvent!");
    }

    private record FXKeyboardEvent(KeyEvent event, KeyCode key) implements KeyboardEvent {
        @Override
        public KeyCode.Modifiers modifiers() {
            return FXInput.modifiers(event);
        }

        @Override
        public boolean isConsumed() {
            return event.isConsumed();
        }

        @Override
        public void consume() {
            event.consume();
        }
    }

    private final class FXMouseClickEvent implements MouseClickEvent {
        private final MouseEvent event;

        private FXMouseClickEvent(MouseEvent event) {
            this.event = event;
        }

        @Override
        public Vec2f pos() {
            Point2D pos = canvas.sceneToLocal(event.getSceneX(), event.getSceneY());
            return new Vec2f((float)pos.getX(), (float)pos.getY());
        }

        @Override
        public MouseClickEvent.@Nullable Button button() {
            return switch (event.getButton()) {
                case PRIMARY -> MouseClickEvent.Button.PRIMARY;
                case MIDDLE -> MouseClickEvent.Button.MIDDLE;
                case SECONDARY -> MouseClickEvent.Button.SECONDARY;
                case NONE -> null;
                default -> MouseClickEvent.Button.OTHER;
            };
        }

        @Override
        public int clickCount() {
            return event.getClickCount();
        }

        @Override
        public KeyCode.Modifiers modifiers() {
            return FXInput.modifiers(event);
        }

        @Override
        public boolean isConsumed() {
            return event.isConsumed();
        }

        @Override
        public void consume() {
            event.consume();
        }
    }
}