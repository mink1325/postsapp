package com.mkcode.postsapi.service;

import static java.lang.String.format;

public class UnknownOperationException extends RuntimeException{
    public UnknownOperationException(String operation) {
        super(format("unknown operator: %s", operation));
    }
}
