package com.mkcode.postsapi.service;

public enum Operator {
    EQUAL(2),
    AND(2),
    OR(2),
    NOT(1),
    GREATER_THAN(2),
    LESS_THAN(2);

    private int numberOfParameters;

    Operator(int numberOfParameters) {
        this.numberOfParameters = numberOfParameters;
    }

    public int getNumberParameters() {
        return numberOfParameters;
    }
}
