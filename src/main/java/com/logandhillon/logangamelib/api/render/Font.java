package com.logandhillon.logangamelib.api.render;

public record Font(String family, float size, FontWeight weight) {
    public enum FontWeight { NORMAL, MEDIUM, BOLD }
}
