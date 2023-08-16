package com.vocbuild.backend.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.util.IOUtils;
import com.vocbuild.backend.exceptions.ServerException;
import com.vocbuild.backend.model.SubtitleModel;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;
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

    @Qualifier("OpenSearchService")
    @Autowired
    SearchServiceInterface searchService;

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
}
