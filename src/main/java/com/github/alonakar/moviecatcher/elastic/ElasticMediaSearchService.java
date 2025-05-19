package com.github.alonakar.moviecatcher.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.get.GetResult;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.github.alonakar.moviecatcher.dto.MediaDto;
import com.github.alonakar.moviecatcher.exception.ApplicationRuntimeException;
import com.github.alonakar.moviecatcher.mapper.MediaMapper;
import com.github.alonakar.moviecatcher.model.MediaContext;
import com.github.alonakar.moviecatcher.model.MediaSearchQuery;
import com.github.alonakar.moviecatcher.service.MediaSearchService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ElasticMediaSearchService implements MediaSearchService {
    public static final String MEDIA_INDEX_PREFIX = "media_";
    private final ElasticsearchClient client;

    public ElasticMediaSearchService(final ElasticsearchClient client) {
        this.client = client;
    }

    @Override
    public List<MediaDto> search(final MediaSearchQuery searchQuery) {
        String index = MEDIA_INDEX_PREFIX + searchQuery.getLang();

        SearchRequest.Builder builder = new SearchRequest.Builder()
                .index(index)
                .query(q -> q.multiMatch(m -> m
                        .query(searchQuery.getQuery())
                        .fields("original_title", "overview", "cast.name")
                        .type(TextQueryType.MostFields)
                        .operator(Operator.Or)))
                .sort(s -> s.field(f -> f.field("_score").order(SortOrder.Desc)))
                .sort(s -> s.field(f -> f.field("popularity").order(SortOrder.Desc)))
                .sort(s -> s.field(f -> f.field("uid").order(SortOrder.Desc)))
                .from(searchQuery.getOffset())
                .size(searchQuery.getSize());

        SearchResponse<IndexedMedia> response;
        try {
            response = client.search(builder.build(), IndexedMedia.class);
        } catch (IOException e) {
            throw new ApplicationRuntimeException("Search is failed");
        }
        List<MediaDto> results = new ArrayList<>();
        for (Hit<IndexedMedia> hit : response.hits().hits()) {
            IndexedMedia indexedMedia = hit.source();
            if (indexedMedia != null) {
                results.add(MediaMapper.toDto(indexedMedia));
            }
        }

        return results;
    }

    @Override
    public MediaContext getById(final String id, final String lang) {
        String indexName = MEDIA_INDEX_PREFIX + lang;

        try {
            return Optional.ofNullable(client.get(g -> g.index(indexName).id(id), IndexedMedia.class))
                    .filter(GetResult::found)
                    .map(GetResult::source)
                    .map(MediaMapper::toContext)
                    .orElse(null);
        } catch (IOException e) {
            throw new ApplicationRuntimeException("Unable to get media by id " + id);
        }
    }

}
