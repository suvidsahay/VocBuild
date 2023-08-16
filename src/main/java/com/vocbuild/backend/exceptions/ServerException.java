package com.vocbuild.backend.exceptions;

public class ServerException extends VocBuildException {

    public static final ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

    public ServerException() {
        super(errorCode);
    }

    public ServerException(String errorMessage) {
        super(errorCode, errorMessage);
    }

    public ServerException(Throwable cause) {
        super(errorCode, cause);
    }
}
