package com.mkcode.postsapi.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class OperationTest {

    @ParameterizedTest
    @CsvSource({"EQUAL,2", "AND,2", "OR,2", "NOT,1", "GREATER_THAN,2", "GREATER_THAN,2"})
    void testValidNumberOfParameters(String operator, int numberOfParameters) {
        assertDoesNotThrow(() -> new Operation(operator, getParametersList(numberOfParameters)));
    }

    @ParameterizedTest
    @CsvSource({"EQUAL,1", "AND,3", "OR,4", "NOT,5", "GREATER_THAN,6", "GREATER_THAN,1"})
    void testInvalidNumberOfParameters(String operator, int numberOfParameters) {
        assertThatThrownBy(() -> new Operation(operator, getParametersList(numberOfParameters)))
                .isExactlyInstanceOf(InvalidNumberOfParametersException.class);
    }

    private List<String> getParametersList(int numberOfParameters) {
        return IntStream.rangeClosed(1, numberOfParameters)
                .mapToObj(String::valueOf)
                .collect(toList());
    }
}