package com.github.alonakar.moviecatcher.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MediaType {
    MOVIE,
    TV;

    @JsonValue
    public String toLowerCase() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static MediaType fromString(String value) {
        return value == null ? null : MediaType.valueOf(value.toUpperCase());
    }
}
