package com.mkcode.postsapi.service;

import static java.lang.String.format;

public class ExpressionValidationException extends RuntimeException{
    public ExpressionValidationException(String expression) {
        super(format("Expression '%s' is not valid", expression));
    }
}
