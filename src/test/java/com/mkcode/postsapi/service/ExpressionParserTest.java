package com.mkcode.postsapi.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static com.mkcode.postsapi.service.ExpressionParser.parseOperation;
import static com.mkcode.postsapi.service.Operator.AND;
import static com.mkcode.postsapi.service.Operator.EQUAL;
import static com.mkcode.postsapi.service.Operator.GREATER_THAN;
import static com.mkcode.postsapi.service.Operator.LESS_THAN;
import static com.mkcode.postsapi.service.Operator.NOT;
import static com.mkcode.postsapi.service.Operator.OR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ExpressionParserTest {

    static Stream<Arguments> validExpressions() {
        return Stream.of(
                arguments("EQUAL(id, \"first-post\")", EQUAL, List.of("id", "\"first-post\"")),
                arguments("EQUAL(views,100)", EQUAL, List.of("views", "100")),
                arguments("AND(EQUAL(id,\"first-post\"),EQUAL(views,100))",
                        AND, List.of("EQUAL(id,\"first-post\")", "EQUAL(views,100)")),
                arguments("OR(EQUAL(id,\"first,post\"),EQUAL(id,\"second-post\"))",
                        OR, List.of("EQUAL(id,\"first,post\")", "EQUAL(id,\"second-post\")")),
                arguments("NOT(EQUAL(id,\"first-post\"))", NOT, List.of("EQUAL(id,\"first-post\")")),
                arguments("GREATER_THAN(views,100)", GREATER_THAN, List.of("views", "100")),
                arguments("LESS_THAN(views,100)", LESS_THAN, List.of("views", "100")));
    }

    @ParameterizedTest
    @MethodSource("validExpressions")
    void testValidExpressions(String expression, Operator expectedOperator, List<String> expectedParameters) {
        var actual = parseOperation(expression);
        assertThat(actual.getOperator()).isEqualTo(expectedOperator);
        assertThat(actual.getParameters()).containsExactlyElementsOf(expectedParameters);
    }

    @ParameterizedTest
    @ValueSource(strings = {"someOperation(1,2)", "AND", "OR(", "OR)", "NOT()", "NOT(   )"})
    void testInvalidExpressions(String expression) {
        assertThatThrownBy(() -> parseOperation(expression))
                .isExactlyInstanceOf(ExpressionValidationException.class);
    }
}