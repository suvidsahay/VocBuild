package com.vocbuild.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDbConfig {
    private final ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
    private final Region region = Region.AP_SOUTH_1;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(region)
//                .credentialsProvider(credentialsProvider)
                .build();
    }
}
