package com.github.alonakar.moviecatcher.facade;

import com.github.alonakar.moviecatcher.dto.MediaDetailDto;
import com.github.alonakar.moviecatcher.exception.ApplicationRuntimeException;
import com.github.alonakar.moviecatcher.model.CastMember;
import com.github.alonakar.moviecatcher.model.ExternalIds;
import com.github.alonakar.moviecatcher.model.MediaContext;
import com.github.alonakar.moviecatcher.model.MediaType;
import com.github.alonakar.moviecatcher.service.MediaSearchService;
import com.github.alonakar.moviecatcher.tmdb.TmdbMediaProviderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.ResourceNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MediaProviderFacadeTest {
    @Mock
    private MediaSearchService mediaSearchService;
    @Mock
    private TmdbMediaProviderService tmdbMediaProviderService;

    private MediaProviderFacade mediaProviderFacade;

    private MediaContext mediaContext;
    private final String id = "uid-00001";
    private final String lang = "en";

    @BeforeEach
    void setUp() {
        mediaProviderFacade = new MediaProviderFacade(mediaSearchService, tmdbMediaProviderService);
        mediaContext = new MediaContext(
                id,
                "The Matrix",
                "Neo discovers the truth",
                9.8,
                "poster.jpg",
                MediaType.MOVIE,
                new ExternalIds("603"),
                List.of(new CastMember("Keanu Reeves"), new CastMember("Laurence Fishburne"))
        );
    }

    @Test
    void shouldReturnMediaDetails_whenMediaExists() {
        // Given
        when(mediaSearchService.getById(id, lang)).thenReturn(mediaContext);
        when(tmdbMediaProviderService.fetchTrailerUrl("603", MediaType.MOVIE)).thenReturn("http://youtube.com/trailer");

        // When
        MediaDetailDto result = mediaProviderFacade.getById(id, lang);

        // Then
        assertEquals(id, result.getId());
        assertEquals("The Matrix", result.getTitle());
        assertEquals("http://youtube.com/trailer", result.getTrailerUrl());
        assertEquals(2, result.getCast().size());
    }

    @Test
    void shouldThrowNotFound_whenMediaIsMissing() {
        when(mediaSearchService.getById(id, lang)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            mediaProviderFacade.getById(id, lang);
        });
    }

    @Test
    void shouldThrowRuntimeException_whenTmdbIdIsBlank() {
        MediaContext contextWithoutTmdb = new MediaContext(
                id,
                "Unknown Film",
                "Mystery",
                5.0,
                null,
                MediaType.MOVIE,
                new ExternalIds(""),
                List.of()
        );

        when(mediaSearchService.getById(id, lang)).thenReturn(contextWithoutTmdb);

        assertThrows(ApplicationRuntimeException.class, () -> {
            mediaProviderFacade.getById(id, lang);
        });
    }
}
