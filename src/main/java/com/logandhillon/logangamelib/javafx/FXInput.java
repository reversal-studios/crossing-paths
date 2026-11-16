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

    private final CopyOnWriteArrayList<Consumer<KeyboardEvent>> keyPressedHandlers  =
            new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Consumer<KeyboardEvent>> keyReleasedHandlers =
            new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Consumer<KeyboardEvent>> textInputHandlers   =
            new CopyOnWriteArrayList<>();

    private final CopyOnWriteArrayList<Consumer<MouseClickEvent>> pointerPressedHandlers  =
            new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Consumer<MouseClickEvent>> pointerReleasedHandlers =
            new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Consumer<MouseClickEvent>> pointerClickedHandlers  =
            new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Consumer<MouseClickEvent>> pointerMovedHandlers    =
            new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Consumer<MouseClickEvent>> pointerDraggedHandlers  =
            new CopyOnWriteArrayList<>();

    private final EventHandler<KeyEvent> keyEventHandler = this::handleKeyEvent;

    private final EventHandler<MouseEvent> pointerPressedHandler  =
            e -> dispatch(pointerPressedHandlers, new FXMouseClickEvent(e));
    private final EventHandler<MouseEvent> pointerReleasedHandler =
            e -> dispatch(pointerReleasedHandlers, new FXMouseClickEvent(e));
    private final EventHandler<MouseEvent> pointerClickedHandler  =
            e -> dispatch(pointerClickedHandlers, new FXMouseClickEvent(e));
    private final EventHandler<MouseEvent> pointerMovedHandler    =
            e -> dispatch(pointerMovedHandlers, new FXMouseClickEvent(e));
    private final EventHandler<MouseEvent> pointerDraggedHandler  =
            e -> dispatch(pointerDraggedHandlers, new FXMouseClickEvent(e));

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
        subscribe(keyPressedHandlers, handler);
    }

    @Override
    public void onKeyUp(Consumer<KeyboardEvent> handler) {
        subscribe(keyReleasedHandlers, handler);
    }

    @Override
    public void onKeyTyped(Consumer<KeyboardEvent> handler) {
        subscribe(textInputHandlers, handler);
    }

    @Override
    public void onMouseDown(Consumer<MouseClickEvent> handler) {
        subscribe(pointerPressedHandlers, handler);
    }

    @Override
    public void onMouseUp(Consumer<MouseClickEvent> handler) {
        subscribe(pointerReleasedHandlers, handler);
    }

    @Override
    public void onMouseClicked(Consumer<MouseClickEvent> handler) {
        subscribe(pointerClickedHandlers, handler);
    }

    @Override
    public void onMouseMoved(Consumer<MouseClickEvent> handler) {
        subscribe(pointerMovedHandlers, handler);
    }

    @Override
    public void onMouseDragged(Consumer<MouseClickEvent> handler) {
        subscribe(pointerDraggedHandlers, handler);
    }

    private <T> void subscribe(CopyOnWriteArrayList<Consumer<T>> handlers, Consumer<T> handler) {
        if (closed) throw new IllegalStateException("Input adapter is closed");
        if (handler == null) throw new NullPointerException("handler");

        handlers.add(handler);
        subscriptions.add(new Subscription(() -> handlers.remove(handler)));
    }

    private void handleKeyEvent(KeyEvent event) {
        KeyCode key = KeyCode.of(event.getCode().getCode());
        dispatch(keyPressedHandlers, new FXKeyboardEvent(event, key));
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

        keyPressedHandlers.clear();
        keyReleasedHandlers.clear();
        textInputHandlers.clear();
        pointerPressedHandlers.clear();
        pointerReleasedHandlers.clear();
        pointerClickedHandlers.clear();
        pointerMovedHandlers.clear();
        pointerDraggedHandlers.clear();
    }

    private static KeyCode.Modifiers modifiers(InputEvent event) {
        if (event instanceof KeyEvent keyEvent) {
            return new KeyCode.Modifiers(
                    keyEvent.isShiftDown(),
                    keyEvent.isControlDown(),
                    keyEvent.isAltDown(),
                    keyEvent.isMetaDown());
        }

        MouseEvent mouseEvent = (MouseEvent)event;
        return new KeyCode.Modifiers(
                mouseEvent.isShiftDown(),
                mouseEvent.isControlDown(),
                mouseEvent.isAltDown(),
                mouseEvent.isMetaDown());
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