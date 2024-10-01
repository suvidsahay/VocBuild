package com.vocbuild.backend.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.util.IOUtils;
import com.vocbuild.backend.exceptions.ServerException;
import com.vocbuild.backend.exceptions.ValidationException;
import com.vocbuild.backend.model.MovieDetails;
import com.vocbuild.backend.model.SubtitleModel;
import com.vocbuild.backend.repository.MovieDetailsRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Service
public class VideoService {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Autowired
    AmazonS3 amazonS3;

    @Autowired
    OMDBService omdbService;

    @Qualifier("OpenSearchService")
    @Autowired
    SearchServiceInterface searchService;

    @Autowired
    MovieDetailsRepository movieDetailsRepository;

    public ResponseEntity<StreamingResponseBody> getClip(@NonNull final String word, final int pageNumber) {
        try {
            byte[] media = findAndGetFile(word, pageNumber);

            final HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("Content-Type", "video/mp4");
            responseHeaders.add("Content-Length", Integer.toString(media.length));

            StreamingResponseBody responseBody = outputStream -> {
                outputStream.write(media);
                outputStream.flush();
            };

            return new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.OK);
        } catch (IOException ex) {
            throw new ServerException(ex);
        }
    }

    public ResponseEntity<StreamingResponseBody> getClip(@NonNull final String word, final int pageNumber, @NonNull final String rangeHeader) {
        try {
            byte[] file = findAndGetFile(word, pageNumber);

            String[] ranges = rangeHeader.split("-");
            int rangeStart = Integer.parseInt(ranges[0].substring(6));
            int rangeEnd;
            if (ranges.length > 1) {
                rangeEnd = Integer.parseInt(ranges[1]);
            } else {
                rangeEnd = file.length - 1;
            }

            if (file.length < rangeEnd) {
                rangeEnd = file.length - 1;
            }

            byte[] media = ArrayUtils.subarray(file, rangeStart, rangeEnd + 1);

            StreamingResponseBody responseBody = outputStream -> {
                outputStream.write(media);
                outputStream.flush();
            };

            final HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("Content-Type", "video/mp4");
            responseHeaders.add("Content-Length", Integer.toString(media.length));
            responseHeaders.add("Accept-Ranges", "bytes");
            responseHeaders.add("Content-Range", "bytes" + " " +
                    rangeStart + "-" + rangeEnd + "/" + file.length);

            return new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.PARTIAL_CONTENT);
        } catch (IOException ex) {
            throw new ServerException(ex);
        }
    }

    private byte[] findAndGetFile(@NonNull String word, int pageNumber) throws IOException {
        List<SubtitleModel> subtitles = searchService
                .searchDocumentWithLimits("subtitle", "text", word, pageNumber, 1, SubtitleModel.class);

        InputStream in = amazonS3.getObject(bucketName,
                        String.format("%s/%s.mp4", subtitles.get(0).getMovieName(), subtitles.get(0).getSeq()))
                .getObjectContent();
        return IOUtils.toByteArray(in);
    }

    public SubtitleModel getClipMetadata(@NonNull final String word,
            @NonNull final int pageNumber) throws IOException {
        List<SubtitleModel> subtitleModels =
                searchService.searchDocumentWithLimits("subtitle", "text", word, pageNumber, 1, SubtitleModel.class);

        SubtitleModel subtitleModel;
        if(!subtitleModels.isEmpty()) {
            subtitleModel = subtitleModels.get(0);
        } else {
            throw new ValidationException("The given word " + word + " doesn't exist. Please check your input and try again");
        }

        MovieDetails movieDetails = movieDetailsRepository.getMovieDetails(subtitleModel.getId());

        if(StringUtils.isEmpty(movieDetails.getMovieFullName())) {
            // New addition of column movieFullName. The below code is used for back filling the entry.
            movieDetails.setMovieFullName(omdbService.getMovieNameFromIMDBId(subtitleModel.getId()));
            movieDetailsRepository.save(movieDetails);
        }

        subtitleModel.setMovieName(movieDetails.getMovieFullName());

        return subtitleModel;
    }
}
