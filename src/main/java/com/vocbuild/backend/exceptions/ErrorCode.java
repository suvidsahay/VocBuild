package com.vocbuild.backend.exceptions;

public enum ErrorCode {

    GENERIC_ERROR_MESSAGE("GENERIC_ERROR_MESSAGE",
            "We're sorry, but an error has occurred. Please try again later or contact our support team for further assistance. "),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR",
            "Oops, something went wrong on our end. We're sorry for the inconvenience. Our team has been alerted of the issue, and we are working to resolve it as soon as possible. Please try again later or contact our support team if the issue persists."),

    CLIENT_ERR("CLIENT_ERR",
            "We're sorry, but it looks like there was an error with your request. Please double-check the information you entered and try again. If you're still experiencing issues, please clear your browser cache and cookies or try using a different browser. If the problem persists, please contact our support team for further assistance."),

    NOT_FOUND("NOT_FOUND",
            "We're sorry, but we couldn't find the resource or page you were looking for. Please double-check the URL"
                    + " and try again. If you believe this is an error, please contact our support team for further assistance.");

    String errorCode;
    String errorMessage;

    ErrorCode(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static ErrorCode fromCode(String code) {
        for (ErrorCode es : values()) {
            if (es.errorCode.equals(code)) {
                return es;
            }
        }
        return ErrorCode.INTERNAL_SERVER_ERROR;
    }

    public String getMessage() {
        return this.errorMessage;
    }

    public String getCode() {
        return this.errorCode;
    }
}
