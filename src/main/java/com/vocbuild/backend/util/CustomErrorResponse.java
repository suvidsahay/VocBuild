package com.vocbuild.backend.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomErrorResponse {

    private String errorCode;

    private String errorMessage;
}

