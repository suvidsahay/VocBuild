package com.vocbuild.backend.service;

import com.vocbuild.backend.model.ElasticSearchModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.query_dsl.RangeQuery;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "OpenSearchService")
@Slf4j
public class OpenSearchService implements SearchServiceInterface{

    @Autowired
    OpenSearchClient client;

    @Override
    public void createOrUpdateDocument(@NonNull String index, @NonNull ElasticSearchModel model) throws IOException {
        IndexRequest<ElasticSearchModel> indexRequest = new IndexRequest.Builder<ElasticSearchModel>()
                .index(index)
                .document(model)
                .build();
        client.index(indexRequest);
    }

    @Override
    public <T> List<T> searchDocument(@NonNull String index, @NonNull String matchField,
            @NonNull String matchText, @NonNull Class<T> tClass) throws IOException {
        SearchResponse<T> searchResponse = client.search(s -> {
            s.index(index);
            s.query(q ->
                    q.match(t -> t
                            .field(matchField)
                            .query(v -> v.stringValue(matchText))
                    )
            );
            return s;
        }, tClass);

        return getMatches(searchResponse);
    }

    @Override
    public <T> List<T> searchDocumentWithLimits(@NonNull String index, @NonNull String matchField,
            @NonNull String matchText, int from, int size, @NonNull Class<T> tClass)
            throws IOException {

        SearchResponse<T> searchResponse = client.search(s -> {
            s.index(index);
            s.from(from);
            s.size(size);
            s.query(q ->
                    q.match(t -> t
                            .field(matchField)
                            .query(v -> v.stringValue(matchText))
                    )
            );
            return s;
        }, tClass);
        return getMatches(searchResponse);
    }

    @Override
    public <T> long getTotal(@NonNull String index, @NonNull String matchField,
            @NonNull String matchText, @NonNull Class<T> tClass) throws IOException {
        SearchResponse<T> searchResponse = client.search(s -> {
            s.index(index);
            s.from(0);
            s.size(0);
            s.query(q ->
                    q.match(t -> t
                            .field(matchField)
                            .query(v -> v.stringValue(matchText))
                    )
            );
            return s;
        }, tClass);

        return searchResponse.hits().total().value();
    }

    private <T> List<T> getMatches(SearchResponse<T> response) {
        log.info(response.toString());
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
