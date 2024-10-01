package com.vocbuild.backend.model;

import java.time.Duration;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class MovieDetails {
    private String imdbMovieId;
    private String movieName;
    private LocalDate dateAdded;
    private String movieFullName;

    @DynamoDbPartitionKey
    @DynamoDbAttribute(value = "ImdbMovieId")
    public String getImdbMovieId() {
        return imdbMovieId;
    }

    public void setImdbMovieId(String imdbMovieId) {
        this.imdbMovieId = imdbMovieId;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public LocalDate getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getMovieFullName() {
        return movieFullName;
    }

    public void setMovieFullName(String movieFullName) {
        this.movieFullName = movieFullName;
    }
}
