package com.vocbuild.backend.exceptions;

public class ValidationException extends VocBuildException {

    public static final ErrorCode errorCode = ErrorCode.CLIENT_ERR;
    public ValidationException() {
        super(errorCode);
    }

    public ValidationException(String errorMessage) {
        super(errorCode, errorMessage);
    }

    public ValidationException(Throwable cause) {
        super(errorCode, cause);
    }

}
