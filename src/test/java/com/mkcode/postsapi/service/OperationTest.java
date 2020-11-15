package com.mkcode.postsapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class OperationTest {

    @Test
    void testValidOperation() {
        assertDoesNotThrow(() -> new Operation("EQUAL", List.of("id", "\"2\"")));
        assertDoesNotThrow(() -> new Operation("EQUAL", List.of("views", "2")));
        assertDoesNotThrow(() -> new Operation("LESS_THAN", List.of("timestamp", "2")));
        assertDoesNotThrow(() -> new Operation("GREATER_THAN", List.of("timestamp", "2")));
        assertDoesNotThrow(() -> new Operation("NOT", List.of("EQUAL(id,\"2\")")));
        assertDoesNotThrow(() -> new Operation("AND", List.of("EQUAL(id,\"2\")", "EQUAL(id,\"2\")")));
        assertDoesNotThrow(() -> new Operation("OR", List.of("EQUAL(id,\"2\")", "EQUAL(id,\"2\")")));
    }

    @ParameterizedTest
    @CsvSource({"EQUAL,1", "AND,3", "OR,1", "NOT,2", "GREATER_THAN,3", "GREATER_THAN,3"})
    void testInvalidNumberOfParameters(String operator, int numberOfParameters) {
        assertThatThrownBy(() -> new Operation(operator, getParametersList(numberOfParameters)))
                .isExactlyInstanceOf(OperationValidationException.class)
                .hasMessage("Operator %s should have %d parameter(s)", operator,
                        Operator.valueOf(operator).getNumberParameters());
    }

    @ParameterizedTest
    @CsvSource({"EQUAL", "GREATER_THAN", "LESS_THAN"})
    void testInvalidFieldNames(String operator) {
        assertThatThrownBy(() -> new Operation(operator, List.of("created_by", "2")))
                .isExactlyInstanceOf(OperationValidationException.class)
                .hasMessage("created_by is invalid field");
    }

    @ParameterizedTest
    @CsvSource({"GREATER_THAN", "LESS_THAN"})
    void testInvalidIntegerField(String operator) {
        assertThatThrownBy(() -> new Operation(operator, List.of("id", "2")))
                .isExactlyInstanceOf(OperationValidationException.class)
                .hasMessage("Field id is not of integer data type");
    }

    @ParameterizedTest
    @CsvSource({"GREATER_THAN", "LESS_THAN"})
    void testInvalidIntegerValue(String operator) {
        assertThatThrownBy(() -> new Operation(operator, List.of("views", "a")))
                .isExactlyInstanceOf(OperationValidationException.class)
                .hasMessage("Value a is not of valid integer value");
    }

    @Test
    void testTextWithoutDoubleQuates() {
        assertThatThrownBy(() -> new Operation("EQUAL", List.of("id", "a")))
                .isExactlyInstanceOf(OperationValidationException.class)
                .hasMessage("Parameter id value should be enclosed with double quotes");
    }

    private List<String> getParametersList(int numberOfParameters) {
        return IntStream.rangeClosed(1, numberOfParameters)
                .mapToObj(String::valueOf)
                .collect(toList());
    }
}