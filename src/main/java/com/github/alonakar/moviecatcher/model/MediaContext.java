package com.github.alonakar.moviecatcher.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class MediaContext {
    private final String id;
    private final String originalTitle;
    private final String overview;
    private final Double popularity;
    private final String posterUrl;
    private final MediaType type;
    private final ExternalIds externalIds;
    private final List<CastMember> cast;
}
