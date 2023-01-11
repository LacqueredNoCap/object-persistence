package com.github.object.persistence.api.exception;

public class InstallConnectionException extends RuntimeException{
    public InstallConnectionException(String message, Exception exception){
        super(message, exception);
    }
}
