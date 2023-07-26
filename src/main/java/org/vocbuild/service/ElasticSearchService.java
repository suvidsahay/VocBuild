package org.vocbuild.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vocbuild.model.ElasticSearchModel;

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
}
