package com.vocbuild.backend.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WordDetails {
    private Definition definition;

    private List<SubtitleModel> subtitleModel;
}
