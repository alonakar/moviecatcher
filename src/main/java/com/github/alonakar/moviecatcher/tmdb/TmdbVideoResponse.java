package com.github.alonakar.moviecatcher.tmdb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TmdbVideoResponse {
    private List<TmdbVideo> results;
}
