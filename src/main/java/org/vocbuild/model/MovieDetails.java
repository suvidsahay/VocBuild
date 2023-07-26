package org.vocbuild.model;

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
    private String movieS3location;
    private String subtitlesS3location;
    private Duration synchronizationTime;
    private LocalDate dateAdded;

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

    public String getMovieS3location() {
        return movieS3location;
    }

    public void setMovieS3location(String movieS3location) {
        this.movieS3location = movieS3location;
    }

    public String getSubtitlesS3location() {
        return subtitlesS3location;
    }

    public void setSubtitlesS3location(String subtitlesS3location) {
        this.subtitlesS3location = subtitlesS3location;
    }

    public LocalDate getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = dateAdded;
    }
}
