package com.github.alonakar.moviecatcher.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MediaSearchQuery {
    private String query;
    private String lang;
    private int size;
    private int offset;
}
