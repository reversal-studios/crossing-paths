package com.logandhillon.logangamelib.engine;

/**
 * Indicates something went wrong during the initialization of logangamelib.
 *
 * @see LGLGameHandler
 */
public final class LGLInitializationException extends RuntimeException {
    public LGLInitializationException(String msg) {
        super(msg);
    }

    public static LGLInitializationException missingProperty(String property) {
        return new LGLInitializationException("Required system property '" + property + "' is not set!");
    }
}