package com.github.alonakar.moviecatcher.service;

import com.github.alonakar.moviecatcher.dto.MediaDto;
import com.github.alonakar.moviecatcher.model.MediaContext;
import com.github.alonakar.moviecatcher.model.MediaSearchQuery;

import java.util.List;

public interface MediaSearchService {
    List<MediaDto> search(MediaSearchQuery build);

    MediaContext getById(String id, String lang);
}
