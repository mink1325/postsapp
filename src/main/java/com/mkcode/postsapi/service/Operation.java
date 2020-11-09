package com.mkcode.postsapi.service;

import lombok.Getter;

import java.util.List;

@Getter
public class Operation {
    private final Operator operator;
    private final List<String> parameters;

    public Operation(String operator, List<String> parameters) {
        this.operator = Operator.valueOf(operator);
        this.parameters = parameters;
        validateParameters();
    }

    private void validateParameters() {
        if (operator.getNumberParameters() != parameters.size()) {
            throw new InvalidNumberOfParametersException(operator);
        }
    }
}