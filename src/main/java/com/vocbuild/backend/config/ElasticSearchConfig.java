package com.vocbuild.backend.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {

    @Value("${spring.elasticsearch.url}")
    private String serverUrl;

    @Value("${spring.elasticsearch.key}")
    private String apiKey;

    @Bean
    public RestClient getRestClient() {
        return RestClient.builder(HttpHost.create(serverUrl))
                .setDefaultHeaders(new Header[]{
                        new BasicHeader("Authorization", "ApiKey " + apiKey)
                })
                .build();
    }

    @Bean
    public ElasticsearchTransport getTransport() {
        return new RestClientTransport(getRestClient(), new JacksonJsonpMapper());
    }

    @Bean
    public ElasticsearchClient getEsClient() {
        return new ElasticsearchClient(getTransport());
    }
}
