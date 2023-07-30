package com.vocbuild.backend.repository;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.vocbuild.backend.model.MovieDetails;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Repository
@AllArgsConstructor
public class MovieDetailsRepository {
    @Autowired
    DynamoDbClient client;

    private final DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(client)
                .build();

    private final DynamoDbTable<MovieDetails> movieTable =
            enhancedClient.table("MovieDetails",
                    TableSchema.fromBean(MovieDetails.class));


    public void save(@NonNull final MovieDetails movieDetails) {
        movieTable.putItem(movieDetails);
    }

    public MovieDetails getMovieDetails(String movieId) {
        Key key = Key.builder()
                .partitionValue(movieId)
                .build();

        return movieTable.getItem(key);
    }
}
