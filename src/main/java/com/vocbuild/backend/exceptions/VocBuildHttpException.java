package com.vocbuild.backend.exceptions;

public class VocBuildHttpException extends VocBuildException {
    public static final ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    public VocBuildHttpException() {
        super(errorCode);
    }

    public VocBuildHttpException(String errorMessage) {
        super(errorCode, errorMessage);
    }

    public VocBuildHttpException(Throwable cause) {
        super(errorCode, cause);
    }
}
