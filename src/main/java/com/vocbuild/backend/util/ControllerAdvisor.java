package com.vocbuild.backend.util;

import com.vocbuild.backend.exceptions.ErrorCode;
import com.vocbuild.backend.exceptions.ServerException;
import com.vocbuild.backend.exceptions.ValidationException;
import com.vocbuild.backend.exceptions.VocBuildException;
import com.vocbuild.backend.exceptions.VocBuildHttpException;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice()
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ControllerAdvisor extends Exception {

    @ExceptionHandler(value = {ServerException.class, VocBuildHttpException.class})
    public final ResponseEntity<CustomErrorResponse> handleInternalServerException(final VocBuildException ex) {
        log.error("ServerException:[{}] [{}] [{}]", ex.getErrorCode(), ex.getErrorMessage(), ex);
        return buildExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, String.valueOf(ex.getErrorCode()),
                ex.getErrorMessage());
    }

    @ExceptionHandler(value = {ValidationException.class})
    public final ResponseEntity<CustomErrorResponse> handleResourceNotFoundException(final VocBuildException ex) {
        log.info("ValidationException:[{}] [{}] [{}]", ex.getErrorCode(), ex.getErrorMessage(), ex);
        return buildExceptionResponse(HttpStatus.NOT_FOUND, String.valueOf(ex.getErrorCode()), ex.getErrorMessage());
    }

    protected ResponseEntity<CustomErrorResponse> buildExceptionResponse(HttpStatus httpStatus, String errorCode,
            String errorMessage) {
        if (Objects.isNull(errorCode) || errorCode.isEmpty()) {
            errorCode = String.valueOf(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        CustomErrorResponse exceptionResponse = CustomErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();

        return new ResponseEntity<>(exceptionResponse, httpStatus);
    }
}
