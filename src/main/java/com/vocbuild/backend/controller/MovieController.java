package com.vocbuild.backend.controller;

import com.vocbuild.backend.exceptions.ValidationException;
import com.vocbuild.backend.model.SubtitleModel;
import com.vocbuild.backend.service.ProcessSubtitlesService;
import com.vocbuild.backend.service.VideoService;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/movie/")
public class MovieController {
    @Autowired
    private ProcessSubtitlesService processSubtitlesService;

    @Autowired
    private VideoService videoService;

    @PostMapping("upload")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public String uploadSubtitles(@RequestPart("file") MultipartFile file,
            @RequestPart("movie_imdb_id") String movieId,
            @RequestPart("movie_name") String movieName) {
        processSubtitlesService.processSubtitles(file, movieId, movieName);
        return "File uploaded successfully";
    }

    @GetMapping("clip/{word}/{pageNumber}")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<Resource> getClip(@PathVariable String word, @PathVariable int pageNumber)
            throws IOException {
        byte[] media = videoService.getClip(word, pageNumber);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new ByteArrayResource(media));
    }

    @PostMapping("subtitles")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public List<SubtitleModel> getSubtitles(@RequestPart("file") MultipartFile file) {
        return processSubtitlesService.processAndReturnSubtitles(file);
    }

}
