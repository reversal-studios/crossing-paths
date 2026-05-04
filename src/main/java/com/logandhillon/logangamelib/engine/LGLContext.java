package com.logandhillon.logangamelib.engine;

/**
 * @author Logan Dhillon
 */
public class LGLContext {
    private static LGLGameHandler<?> instance;

    public static void register(LGLGameHandler<?> handler) {
        instance = handler;
    }

    public static LGLGameHandler<?> getInstance() {
        if (instance == null) throw new IllegalStateException("No handler registered");
        return instance;
    }
}