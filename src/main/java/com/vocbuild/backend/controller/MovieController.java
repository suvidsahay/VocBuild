package com.vocbuild.backend.controller;

import com.vocbuild.backend.model.SubtitleModel;
import com.vocbuild.backend.service.ProcessSubtitlesService;
import com.vocbuild.backend.service.VideoService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

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
        return processSubtitlesService.processSubtitles(file, movieId, movieName);
    }

    @GetMapping("clip/{word}/{pageNumber}")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public ResponseEntity<StreamingResponseBody> getClip(@PathVariable String word,
            @PathVariable int pageNumber,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {
        if(rangeHeader == null) {
            return videoService.getClip(word, pageNumber);
        }
        return videoService.getClip(word, pageNumber, rangeHeader);
    }

    @PostMapping("subtitles")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public List<SubtitleModel> getSubtitles(@RequestPart("file") MultipartFile file) {
        return processSubtitlesService.processAndReturnSubtitles(file);
    }

}
