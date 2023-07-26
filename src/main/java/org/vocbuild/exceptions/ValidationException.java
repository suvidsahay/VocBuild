package org.vocbuild.exceptions;

import lombok.NonNull;

public class ValidationException extends RuntimeException{
    public ValidationException(@NonNull final String message) {
        super(message);
    }

}
