package com.mkcode.postsapi.service;

public class OperationValidationException extends RuntimeException {
    public OperationValidationException(String message) {
        super(message);
    }
}
