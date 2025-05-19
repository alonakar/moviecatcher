package com.github.alonakar.moviecatcher.controller;

import com.github.alonakar.moviecatcher.dto.MediaDetailDto;
import com.github.alonakar.moviecatcher.dto.MediaDto;
import com.github.alonakar.moviecatcher.facade.MediaProviderFacade;
import com.github.alonakar.moviecatcher.model.MediaSearchQuery;
import com.github.alonakar.moviecatcher.service.MediaSearchService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/media")
public class MediaController {

    private final MediaSearchService mediaSearchService;
    private final MediaProviderFacade mediaProviderFacade;

    public MediaController(MediaSearchService mediaSearchService, MediaProviderFacade mediaProviderService) {
        this.mediaSearchService = mediaSearchService;
        this.mediaProviderFacade = mediaProviderService;
    }

    @GetMapping("/search")
    public List<MediaDto> searchMovies(
            @RequestParam String query,
            @RequestParam(defaultValue = "en") String lang,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize,
            @RequestParam(defaultValue = "1") @Min(1) int pageNumber) {
        return mediaSearchService.search(MediaSearchQuery.builder()
                .query(query)
                .lang(lang)
                .size(pageSize)
                .offset((pageNumber - 1) * pageSize)
                .build());
    }

    @GetMapping("/{id}")
    public MediaDetailDto getById(@PathVariable String id, @RequestParam(defaultValue = "en") String lang) {
        return mediaProviderFacade.getById(id, lang);
    }
}
