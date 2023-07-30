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
public class ElasticSearchService {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    public void createOrUpdateDocument(@NonNull final String index, @NonNull final ElasticSearchModel model) {
        try {
            IndexResponse response = elasticsearchClient.index(i -> i
                    .index(index)
                    .document(model)
            );

            log.info(response.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> searchDocument(
            @NonNull final String index,
            @NonNull final String matchField,
            @NonNull final String matchText,
            @NonNull final Class<T> tClass) {
        try {
            SearchResponse<T> response = elasticsearchClient.search(s -> s
                            .index(index)
                            .query(q -> q
                                    .match(t -> t
                                            .field(matchField)
                                            .query(matchText)
                                    )
                            ),
                    tClass);

            List<Hit<T>> hits = response.hits().hits();
            List<T> matches = new ArrayList<>();
            for (Hit<T> hit: hits) {
                T model = hit.source();
                log.info("Found model " + model);
                matches.add(model);
            }

            return matches;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
