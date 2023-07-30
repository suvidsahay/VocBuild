package com.vocbuild.backend.controller;

import com.vocbuild.backend.service.ProcessSubtitlesService;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class SubtitlesController {
    @Autowired
    private ProcessSubtitlesService processSubtitlesService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public String uploadSubtitles(@RequestPart("file") MultipartFile file,
            @RequestPart("movie_imdb_id") String movieId,
            @RequestPart("movie_name") String movieName) {
        processSubtitlesService.processSubtitles(file, movieId, movieName);
        return "File uploaded successfully";
    }
}
