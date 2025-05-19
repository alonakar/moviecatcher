package com.github.alonakar.moviecatcher.dto;

import com.github.alonakar.moviecatcher.model.CastMember;
import com.github.alonakar.moviecatcher.model.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class MediaDetailDto {

    private String id;
    private String title;
    private MediaType type;
    private String overview;
    private String posterUrl;
    private List<CastMember> cast;
    private String trailerUrl;

}