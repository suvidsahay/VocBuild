package com.vocbuild.backend.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.util.IOUtils;
import com.vocbuild.backend.model.SubtitleModel;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VideoService {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Autowired
    AmazonS3 amazonS3;

    @Qualifier("OpenSearchService")
    @Autowired
    SearchServiceInterface searchService;

    public byte[] getClip(@NonNull final String word, final int pageNumber) throws IOException {
        List<SubtitleModel> subtitles = searchService
                .searchDocumentWithLimits("subtitle", "text", word, pageNumber, 1, SubtitleModel.class);

        InputStream in = amazonS3.getObject(bucketName,
                        String.format("%s/%s.mkv", subtitles.get(0).getMovieName(), subtitles.get(0).getSeq()))
                .getObjectContent();

        return IOUtils.toByteArray(in);
    }
}
