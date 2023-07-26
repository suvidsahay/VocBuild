package org.vocbuild.service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.vocbuild.model.MovieDetails;
import org.vocbuild.model.SubtitleModel;
import org.vocbuild.repository.MovieDetailsRepository;
import org.vocbuild.util.SubtitlesParser;

@Service
public class ProcessSubtitlesService {

    @Autowired
    private SubtitlesParser subtitlesParser;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private MovieDetailsRepository movieDetailsRepository;

    public void processSubtitles(@NonNull final MultipartFile file,
            @NonNull final String imdbMovieId,
            @NonNull final String movieName,
            @NonNull final String subtitlesS3Location,
            final Duration synchronizationTime) {
        try {
            if(movieDetailsRepository.getMovieDetails(imdbMovieId) != null) {
                return;
            }
            final MovieDetails movieDetails = MovieDetails.builder()
                    .imdbMovieId(imdbMovieId)
                    .movieName(movieName)
                    .subtitlesS3location(subtitlesS3Location)
                    .synchronizationTime(synchronizationTime)
                    .dateAdded(LocalDate.now())
                    .build();
            movieDetailsRepository.save(movieDetails);
            for(SubtitleModel subtitleModel : subtitlesParser.parseSubtitles(file)) {
                subtitleModel.setId(imdbMovieId);
                elasticSearchService.createOrUpdateDocument("subtitle", subtitleModel);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
