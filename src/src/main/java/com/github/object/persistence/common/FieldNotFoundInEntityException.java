package com.github.object.persistence.common;

public class FieldNotFoundInEntityException extends RuntimeException{
    public FieldNotFoundInEntityException(String message) {
        super(message);
    }
}
