package com.vocbuild.backend.controller;

import com.vocbuild.backend.model.Definition;
import com.vocbuild.backend.model.WordDetails;
import com.vocbuild.backend.service.WordDetailsService;
import java.io.IOException;
import java.util.List;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/word")
public class WordController {

    @Autowired
    WordDetailsService wordDetailsService;

    @GetMapping("/details")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public WordDetails getOccurrences(@RequestParam("word") String word) throws HttpException, IOException {
        return wordDetailsService.getWordDetails(word);
    }

    @GetMapping("/definition")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public Definition getDefinition(@RequestParam("word") String word) throws HttpException {
        return wordDetailsService.getMeaning(word);
    }

    @GetMapping("/total/{word}")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public long getTotal(@PathVariable("word") String word) throws IOException {
        return wordDetailsService.getTotal(word);
    }
}
