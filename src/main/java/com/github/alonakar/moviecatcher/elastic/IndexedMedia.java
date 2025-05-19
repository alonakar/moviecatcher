package com.github.alonakar.moviecatcher.elastic;

import com.github.alonakar.moviecatcher.model.CastMember;
import com.github.alonakar.moviecatcher.model.ExternalIds;
import com.github.alonakar.moviecatcher.model.MediaType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "media_en")
public class IndexedMedia {
    @JsonProperty("uid")
    private String id;
    @JsonProperty("original_title")
    private String originalTitle;
    private String overview;
    private double popularity;
    private String posterUrl;
    private MediaType type;
    private List<CastMember> cast;
    @JsonProperty("external_ids")
    private ExternalIds externalIds;

}
