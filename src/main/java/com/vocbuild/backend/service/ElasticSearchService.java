package com.vocbuild.backend.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vocbuild.backend.model.ElasticSearchModel;

@Service
@Slf4j
public class ElasticSearchService implements SearchServiceInterface {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Override
    public void createOrUpdateDocument(@NonNull final String index, @NonNull final ElasticSearchModel model)
            throws IOException {
        IndexResponse response = elasticsearchClient.index(i -> i
                .index(index)
                .document(model)
        );

        log.info(response.toString());

    }

    @Override
    public <T> List<T> searchDocument(
            @NonNull final String index,
            @NonNull final String matchField,
            @NonNull final String matchText,
            @NonNull final Class<T> tClass) throws IOException {
        SearchResponse<T> response = elasticsearchClient.search(s -> s
                        .index(index)
                        .query(q -> q
                                .match(t -> t
                                        .field(matchField)
                                        .query(matchText)
                                )
                        ),
                tClass);

        return getMatches(response);
    }

    @Override
    public <T> List<T> searchDocumentWithLimits(
            @NonNull final String index,
            @NonNull final String matchField,
            @NonNull final String matchText,
            final int from,
            final int size,
            @NonNull final Class<T> tClass) throws IOException {

        SearchResponse<T> response = null;
        response = elasticsearchClient.search(s -> s
                        .index(index)
                        .query(q -> q
                                .match(t -> t
                                        .field(matchField)
                                        .query(matchText)
                                )
                        )
                        .from(from)
                        .size(size),
                tClass);

        return getMatches(response);

    }

    @Override
    public <T> List<T> getAllDocuments(@NonNull String index, @NonNull Class<T> tClass) throws IOException {
        return null;
    }

    @Override
    public <T> long getTotal(@NonNull String index, @NonNull String matchField,
            @NonNull String matchText, @NonNull Class<T> tClass) {
        return 0;
    }

    @Override
    public <T> List<T> searchDocumentWithWildcards(@NonNull String index, @NonNull String matchField,
            @NonNull String matchText, @NonNull Class<T> tClass) throws IOException {
        return null;
    }

    private <T> List<T> getMatches(SearchResponse<T> response) {
        List<Hit<T>> hits = response.hits().hits();
        List<T> matches = new ArrayList<>();
        for (Hit<T> hit: hits) {
            T model = hit.source();
            log.info("Found model " + model);
            matches.add(model);
        }

        return matches;
    }
}
