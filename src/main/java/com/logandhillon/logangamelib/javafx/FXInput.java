package com.logandhillon.logangamelib.javafx;

import com.logandhillon.logangamelib.api.event.CompositeSubscription;
import com.logandhillon.logangamelib.api.event.Event;
import com.logandhillon.logangamelib.api.event.EventHandler;
import com.logandhillon.logangamelib.api.event.Subscription;
import com.logandhillon.logangamelib.api.input.InputHandler;
import com.logandhillon.logangamelib.api.input.KeyCode;
import com.logandhillon.logangamelib.api.input.KeyboardEvent;
import com.logandhillon.logangamelib.api.input.MouseClickEvent;
import com.logandhillon.logangamelib.math.Vec2f;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

/**
 * Implementation of the LGL input API for JavaFX. Contains list of logangamelib event handlers that are dispatched when
 * the corresponding JavaFX event is fired.
 * <p>
 * The input API can be accessed through a game scene, typically by refering to an entity's parent.
 *
 * @author Logan Dhillon
 * @see com.logandhillon.logangamelib.engine.GameScene#input()
 */
public final class FXInput implements InputHandler, AutoCloseable {
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    private final FXEventHandlers<KeyboardEvent, KeyEvent> keyUp    =
            new FXEventHandlers<>(e -> new FXKeyboardEvent(e, KeyCode.of(e.getCode().getCode()), e.getCharacter()));
    private final FXEventHandlers<KeyboardEvent, KeyEvent> keyDown  =
            new FXEventHandlers<>(e -> new FXKeyboardEvent(e, KeyCode.of(e.getCode().getCode()), e.getCharacter()));
    private final FXEventHandlers<KeyboardEvent, KeyEvent> keyTyped =
            new FXEventHandlers<>(e -> new FXKeyboardEvent(e, KeyCode.of(e.getCode().getCode()), e.getCharacter()));

    private final FXEventHandlers<MouseClickEvent, MouseEvent> mouseDown    =
            new FXEventHandlers<>(FXMouseClickEvent::new);
    private final FXEventHandlers<MouseClickEvent, MouseEvent> mouseUp      =
            new FXEventHandlers<>(FXMouseClickEvent::new);
    private final FXEventHandlers<MouseClickEvent, MouseEvent> mouseClicked =
            new FXEventHandlers<>(FXMouseClickEvent::new);
    private final FXEventHandlers<MouseClickEvent, MouseEvent> mouseMoved   =
            new FXEventHandlers<>(FXMouseClickEvent::new);
    private final FXEventHandlers<MouseClickEvent, MouseEvent> mouseDragged =
            new FXEventHandlers<>(FXMouseClickEvent::new);

    private Scene   scene;
    private Canvas  canvas;
    private boolean closed;

    /**
     * Allows you to construct before the scene is built so game code can register input handlers early. Call
     * {@link attach} once the JavaFX Scene exist.
     */
    public FXInput() {}

    /**
     * Called when the JavaFX {@link Scene} and {@link Canvas} are created so FX's event handlers can be bound to.
     *
     * @param scene  newly built FX scene from {@link com.logandhillon.logangamelib.engine.GameScene}
     * @param canvas graphics canvas from FX scene
     */
    public void attach(Scene scene, Canvas canvas) {
        if (closed) throw new IllegalStateException("Input adapter is closed");
        if (this.scene != null) throw new IllegalStateException("Input adapter is already attached");

        this.scene = scene;
        this.canvas = canvas;

        scene.addEventHandler(KeyEvent.KEY_PRESSED, keyDown);
        scene.addEventHandler(KeyEvent.KEY_RELEASED, keyUp);
        scene.addEventHandler(KeyEvent.KEY_TYPED, keyTyped);

        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseDown);
        scene.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseUp);
        scene.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClicked);
        scene.addEventHandler(MouseEvent.MOUSE_MOVED, mouseMoved);
        scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragged);
    }

    @Override
    public Subscription onKeyDown(EventHandler<KeyboardEvent> handler) {
        return subscribe(keyDown.handlers, handler);
    }

    @Override
    public Subscription onKeyUp(EventHandler<KeyboardEvent> handler) {
        return subscribe(keyUp.handlers, handler);
    }

    @Override
    public Subscription onKeyTyped(EventHandler<KeyboardEvent> handler) {
        return subscribe(keyTyped.handlers, handler);
    }

    @Override
    public Subscription onMouseDown(EventHandler<MouseClickEvent> e) {
        return subscribe(mouseDown.handlers, e);
    }

    @Override
    public Subscription onMouseUp(EventHandler<MouseClickEvent> e) {
        return subscribe(mouseUp.handlers, e);
    }

    @Override
    public Subscription onMouseClicked(EventHandler<MouseClickEvent> e) {
        return subscribe(mouseClicked.handlers, e);
    }

    @Override
    public Subscription onMouseMoved(EventHandler<MouseClickEvent> e) {
        return subscribe(mouseMoved.handlers, e);
    }

    @Override
    public Subscription onMouseDragged(EventHandler<MouseClickEvent> e) {
        return subscribe(mouseDragged.handlers, e);
    }

    @Override
    public void clear() {
        keyUp.handlers.clear();
        keyDown.handlers.clear();
        keyTyped.handlers.clear();

        mouseDown.handlers.clear();
        mouseUp.handlers.clear();
        mouseClicked.handlers.clear();
        mouseMoved.handlers.clear();
        mouseDragged.handlers.clear();
    }

    /**
     * Adds an event handler to the event handler registry, and sets up an auto-unsubscribing {@link Subscription} for
     * memory-safe event binding.
     *
     * @param handlers list of event handlers
     * @param handler  new handler to bind
     * @param <T>      type of LGL event
     *
     * @return reference to subscription, can be used to unsubscribe from this event
     */
    private <T extends Event> Subscription subscribe(
            CopyOnWriteArrayList<EventHandler<T>> handlers, EventHandler<T> handler) {
        if (closed) throw new IllegalStateException("Input adapter is closed");
        if (handler == null) throw new NullPointerException("handler");

        handlers.add(handler);
        Subscription sub = new Subscription(() -> handlers.remove(handler));
        subscriptions.add(sub);
        return sub;
    }

    @Override
    public void close() {
        if (closed) return;
        closed = true;
        subscriptions.close();

        if (scene != null) {
            scene.removeEventHandler(KeyEvent.KEY_PRESSED, keyDown);
            scene.removeEventHandler(KeyEvent.KEY_RELEASED, keyUp);
            scene.removeEventHandler(KeyEvent.KEY_TYPED, keyTyped);

            scene.removeEventHandler(MouseEvent.MOUSE_PRESSED, mouseDown);
            scene.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseUp);
            scene.removeEventHandler(MouseEvent.MOUSE_CLICKED, mouseClicked);
            scene.removeEventHandler(MouseEvent.MOUSE_MOVED, mouseMoved);
            scene.removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragged);
        }

        scene = null;
        canvas = null;

        this.clear();
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

    /**
     * Translates an FX {@link KeyEvent} to an LGL {@link KeyboardEvent}.
     *
     * @param event FX key event details
     * @param key   LGL key code associated with event
     */
    private record FXKeyboardEvent(KeyEvent event, KeyCode key, String character) implements KeyboardEvent {
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

    /**
     * Translates an FX {@link MouseEvent} to an LGL {@link MouseClickEvent}.
     */
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

    /**
     * A group of LGL-FX event handlers, containing a thread-safe list of {@link handlers} while acting as a
     * {@link javafx.event.Event} with a {@link handle} method.
     *
     * @param <LGLEvent> type of LGL-engine event
     * @param <FXEvent>  type of JavaFX event corresponding to the LGL event
     *
     * @see javafx.event.EventHandler
     */
    private final class FXEventHandlers<LGLEvent extends Event, FXEvent extends javafx.event.Event>
            implements javafx.event.EventHandler<FXEvent> {
        private final CopyOnWriteArrayList<EventHandler<LGLEvent>> handlers;
        private final Function<FXEvent, LGLEvent>                  eventSupplier;

        private FXEventHandlers(Function<FXEvent, LGLEvent> eventSupplier) {
            this.handlers = new CopyOnWriteArrayList<>();
            this.eventSupplier = eventSupplier;
        }

        @Override
        public void handle(FXEvent event) {
            for (EventHandler<LGLEvent> handler: handlers) {
                if (closed || event.isConsumed()) return;
                handler.handle(eventSupplier.apply(event));
            }
        }
    }
}

