package com.github.object.persistence.api.exception;

public class FieldNotFoundInEntityException extends RuntimeException{
    public FieldNotFoundInEntityException(String message) {
        super(message);
    }
}
