package com.logandhillon.logangamelib.api.scene;

import com.logandhillon.logangamelib.api.platform.Window;
import com.logandhillon.logangamelib.engine.GameScene;

/**
 * Creates and runs scenes using a particular platform backend.
 *
 * @param <T> platform surface type
 *
 * @author Logan Dhillon
 */
public interface SceneFactory<T> {
    /**
     * Builds a new scene from a game scene and window config.
     *
     * @param scene  LGL game scene to build
     * @param config window config
     *
     * @return scene handle
     */
    SceneHandle<T> build(GameScene<?> scene, Window config);
}
