package com.logandhillon.logangamelib.engine;

import com.logandhillon.logangamelib.resource.FontResource;

public class GameMeta {
    private static GameMeta instance;

    public final String       gameName;
    public final FontResource defaultFont;

    private GameMeta(Builder b) {
        this.gameName = b.gameName;
        this.defaultFont = b.defaultFont;
    }

    public static GameMeta get() {
        if (instance == null) throw new IllegalStateException("GameMeta not registered");
        return instance;
    }

    public static Builder of() {
        return new Builder();
    }

    public static class Builder {
        private String       gameName    = "My logangamelib game";
        private FontResource defaultFont = null;

        public Builder gameName(String name) {
            this.gameName = name; return this;
        }

        public Builder defaultFont(FontResource font) {
            this.defaultFont = font; return this;
        }

        public void register() {
            if (GameMeta.instance != null) throw new IllegalStateException("GameMeta already registered");
            GameMeta.instance = new GameMeta(this);
        }
    }
}