package com.github.alonakar.moviecatcher.mapper;

import com.github.alonakar.moviecatcher.dto.MediaDto;
import com.github.alonakar.moviecatcher.elastic.IndexedMedia;
import com.github.alonakar.moviecatcher.model.MediaContext;

import java.util.List;

public class MediaMapper {

    public static MediaDto toDto(IndexedMedia media) {
        if (media == null) {
            return null;
        }

        return new MediaDto(
                media.getId(),
                media.getOriginalTitle(),
                media.getOverview(),
                media.getPosterUrl(),
                media.getType()
        );
    }

    public static MediaContext toContext(IndexedMedia media) {
        if (media == null) {
            return null;
        }

        return new MediaContext(
                media.getId(),
                media.getOriginalTitle(),
                media.getOverview(),
                media.getPopularity(),
                media.getPosterUrl(),
                media.getType(),
                media.getExternalIds(),
                media.getCast() != null ? List.copyOf(media.getCast()) : List.of()
        );
    }
}