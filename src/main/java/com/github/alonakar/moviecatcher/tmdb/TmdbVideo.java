package com.github.alonakar.moviecatcher.tmdb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TmdbVideo {
    private String site;
    private String key;
    private String type;
    private boolean official;
}
