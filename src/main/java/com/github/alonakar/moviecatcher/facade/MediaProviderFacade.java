package com.github.alonakar.moviecatcher.facade;

import com.github.alonakar.moviecatcher.dto.MediaDetailDto;
import com.github.alonakar.moviecatcher.exception.ApplicationRuntimeException;
import com.github.alonakar.moviecatcher.model.MediaContext;
import com.github.alonakar.moviecatcher.service.MediaSearchService;
import com.github.alonakar.moviecatcher.tmdb.TmdbMediaProviderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MediaProviderFacade {

    private final MediaSearchService mediaSearchService;
    private final TmdbMediaProviderService tmdbMediaProviderService;

    public MediaProviderFacade(final MediaSearchService mediaSearchService,
                               final TmdbMediaProviderService tmdbMediaProviderService) {
        this.mediaSearchService = mediaSearchService;
        this.tmdbMediaProviderService = tmdbMediaProviderService;
    }

    @Cacheable("mediaDetailsCache")
    public MediaDetailDto getById(final String id, final String lang) {
        MediaContext mediaContext = mediaSearchService.getById(id, lang);
        if (mediaContext == null) {
            throw new ResourceNotFoundException("Media with id " + id + " not found");
        }
        String tmdbId = mediaContext.getExternalIds().getTmdb();
        if (StringUtils.isBlank(tmdbId)) {
            throw new ApplicationRuntimeException("Media details for ID are not known");
        }
        String trailerUrl = tmdbMediaProviderService.fetchTrailerUrl(tmdbId, mediaContext.getType());
        return MediaDetailDto.builder()
                .id(mediaContext.getId())
                .type(mediaContext.getType())
                .title(mediaContext.getOriginalTitle())
                .overview(mediaContext.getOverview())
                .posterUrl(mediaContext.getPosterUrl())
                .cast(mediaContext.getCast())
                .trailerUrl(trailerUrl)
                .build();
    }

}
