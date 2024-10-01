package com.vocbuild.backend.config;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.aws.AwsSdk2Transport;
import org.opensearch.client.transport.aws.AwsSdk2TransportOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;

@Configuration
public class OpenSearchConfig {
    @Value("${amazon.aws.accesskey}")
    private String accessKey;

    @Value("${amazon.aws.secretkey}")
    private String secretKey;

    private static final String host = "search-vocbuild-3sxumk5cnlijv2ucvbkdagi44q.ap-south-1.es.amazonaws.com";
    private static Region region = Region.AP_SOUTH_1;


    @Bean
    public OpenSearchClient getClient() {
        AwsCredentialsProvider credentialsProvider =
                StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
        SdkHttpClient httpClient = ApacheHttpClient.builder().build();
        return new OpenSearchClient(
                new AwsSdk2Transport(
                        httpClient,
                        host,
                        region,
                        AwsSdk2TransportOptions.builder().setCredentials(credentialsProvider).build()));

    }
}
