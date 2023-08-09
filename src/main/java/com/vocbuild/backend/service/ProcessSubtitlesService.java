package com.vocbuild.backend.service;

import com.vocbuild.backend.model.SubtitleModel;
import com.vocbuild.backend.repository.MovieDetailsRepository;
import com.vocbuild.backend.util.SubtitlesParser;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.vocbuild.backend.model.MovieDetails;

@Service
public class ProcessSubtitlesService {

    @Autowired
    private SubtitlesParser subtitlesParser;

    @Qualifier("OpenSearchService")
    @Autowired
    private SearchServiceInterface searchService;

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
                searchService.createOrUpdateDocument("subtitle", subtitleModel);
                System.out.println(subtitleModel);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<SubtitleModel> processAndReturnSubtitles(@NonNull final MultipartFile file) {
        try {
            return subtitlesParser.parseSubtitles(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
