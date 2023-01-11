package com.github.object.persistence.api.exception;

public class ReflectionOperationException extends RuntimeException {
    public ReflectionOperationException(String message) {
        super(message);
    }

    public ReflectionOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReflectionOperationException(Throwable cause) {
        super(cause);
    }
}
