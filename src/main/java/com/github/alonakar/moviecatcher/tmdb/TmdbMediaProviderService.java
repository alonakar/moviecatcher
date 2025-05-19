package com.github.alonakar.moviecatcher.tmdb;

import com.github.alonakar.moviecatcher.model.MediaType;
import com.github.alonakar.moviecatcher.service.MediaProviderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TmdbMediaProviderService implements MediaProviderService {

    public static final String TRAILER_TYPE = "Trailer";
    public static final String YOUTUBE_PROVIDER = "YouTube";
    public static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    public static final String VIMEO_PROVIDER = "Vimeo";
    public static final String VIMEO_URL = "https://vimeo.com/";
    private final RestTemplate restTemplate;
    private final String accessToken;
    private final String apiUrl;

    public TmdbMediaProviderService(
            RestTemplate restTemplate,
            @Value("${tmdb.access.token}") String accessToken,
            @Value("${tmdb.api.url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.accessToken = accessToken;
        this.apiUrl = apiUrl;
    }

    @Override
    public String fetchTrailerUrl(final String id, final MediaType type) {
        String url = String.format("%s/%s/%s/videos", apiUrl, type.toLowerCase(), id);
        HttpEntity<Void> request = new HttpEntity<>(getAuthHttpHeaders());

        ResponseEntity<TmdbVideoResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, request, TmdbVideoResponse.class);

        List<TmdbVideo> tmdbVideos = Optional.ofNullable(response.getBody())
                .map(TmdbVideoResponse::getResults)
                .orElse(Collections.emptyList());

        List<TmdbVideo> trailers = tmdbVideos.stream()
                .filter(video -> video.getType().equalsIgnoreCase(TRAILER_TYPE))
                .collect(Collectors.toList());

        TmdbVideo trailerVideo = trailers.stream()
                .filter(TmdbVideo::isOfficial)
                .findFirst().
                orElse(fallBackToNonOfficialTrailer(trailers));

        return buildVideoURL(trailerVideo);
    }

    private HttpHeaders getAuthHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        return headers;
    }

    private static String buildVideoURL(TmdbVideo trailerVideo) {
        if (trailerVideo == null) {
            return StringUtils.EMPTY;
        }
        if (YOUTUBE_PROVIDER.equalsIgnoreCase(trailerVideo.getSite())) {
            return YOUTUBE_URL + trailerVideo.getKey();
        } else if (VIMEO_PROVIDER.equalsIgnoreCase(trailerVideo.getSite())) {
            return VIMEO_URL + trailerVideo.getKey();
        }

        return StringUtils.EMPTY;
    }

    private TmdbVideo fallBackToNonOfficialTrailer(List<TmdbVideo> trailers) {
        return trailers.stream().findFirst().orElse(null);
    }

}
