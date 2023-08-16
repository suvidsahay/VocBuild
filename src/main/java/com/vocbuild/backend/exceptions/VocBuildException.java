package com.vocbuild.backend.exceptions;

import lombok.Getter;

public abstract class VocBuildException extends RuntimeException {
    @Getter
    private final ErrorCode errorCode;

    @Getter
    private final String errorMessage;

    public VocBuildException(ErrorCode errorCode) {
        super(errorCode.getCode());
        this.errorCode = ErrorCode.valueOf(errorCode.getCode());
        this.errorMessage = errorCode.getMessage();
    }

    public VocBuildException(ErrorCode errorCode, String errorMessage) {
        super(errorCode.getCode());
        this.errorCode = ErrorCode.valueOf(errorCode.getCode());
        this.errorMessage = errorMessage;
    }

    public VocBuildException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getCode(), cause);
        this.errorCode = ErrorCode.valueOf(errorCode.getCode());
        this.errorMessage = errorCode.getMessage();
    }
}
