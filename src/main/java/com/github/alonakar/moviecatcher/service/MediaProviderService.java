package com.github.alonakar.moviecatcher.service;

import com.github.alonakar.moviecatcher.model.MediaType;

public interface MediaProviderService {
    String fetchTrailerUrl(String id, MediaType type);
}
