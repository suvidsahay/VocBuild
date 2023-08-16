package com.vocbuild.backend.service;

import com.vocbuild.backend.exceptions.ServerException;
import com.vocbuild.backend.exceptions.ValidationException;
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

    public String processSubtitles(@NonNull final MultipartFile file,
            @NonNull final String imdbMovieId,
            @NonNull final String movieName) {
        if(movieDetailsRepository.getMovieDetails(imdbMovieId) != null) {
            return "The file is already processed in the system";
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
            try {
                searchService.createOrUpdateDocument("subtitle", subtitleModel);
            } catch (IOException e) {
                throw new ServerException(e);
            }
        }
        return "File uploaded successfully";
    }

    public List<SubtitleModel> processAndReturnSubtitles(@NonNull final MultipartFile file) {
        return subtitlesParser.parseSubtitles(file);
    }
}
