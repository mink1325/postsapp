package com.mkcode.postsapi.service;

import static java.lang.String.format;

public class InvalidNumberOfParametersException extends RuntimeException {
    public InvalidNumberOfParametersException(Operator operator) {
        super(format("Operator %s should have %d parameter(s)", operator.name(), operator.getNumberParameters()));
    }
}
