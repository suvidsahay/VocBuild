package org.vocbuild.controller;

import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.vocbuild.model.Definition;
import org.vocbuild.service.DictionaryService;

@RestController
@RequestMapping("/word")
public class WordController {

    @Autowired
    DictionaryService dictionaryService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    public Definition uploadSubtitles(@RequestParam("word") String word) throws HttpException {
        return dictionaryService.getMeaning(word);
    }
}
