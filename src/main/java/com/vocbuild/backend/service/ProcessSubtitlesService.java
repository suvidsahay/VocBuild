package com.vocbuild.backend.service;

import com.vocbuild.backend.model.SubtitleModel;
import com.vocbuild.backend.repository.MovieDetailsRepository;
import com.vocbuild.backend.util.SubtitlesParser;
import java.io.IOException;
import java.time.LocalDate;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.vocbuild.backend.model.MovieDetails;

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
            @NonNull final String movieName) {
        try {
            if(movieDetailsRepository.getMovieDetails(imdbMovieId) != null) {
                return;
            }
            final MovieDetails movieDetails = MovieDetails.builder()
                    .imdbMovieId(imdbMovieId)
                    .movieName(movieName)
                    .dateAdded(LocalDate.now())
                    .build();
            movieDetailsRepository.save(movieDetails);
            for(SubtitleModel subtitleModel : subtitlesParser.parseSubtitles(file)) {
                subtitleModel.setId(imdbMovieId);
                subtitleModel.setMovieName(movieName);
                elasticSearchService.createOrUpdateDocument("subtitle", subtitleModel);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
