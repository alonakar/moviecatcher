package com.github.alonakar.moviecatcher.dto;

import com.github.alonakar.moviecatcher.model.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MediaDto {

    private String id;
    private String title;
    private String overview;
    private String posterUrl;
    private MediaType type;

}