package com.mkcode.postsapi.service;

import lombok.Getter;

import java.util.List;

import static com.mkcode.postsapi.service.Operator.EQUAL;
import static com.mkcode.postsapi.service.Operator.GREATER_THAN;
import static com.mkcode.postsapi.service.Operator.LESS_THAN;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNumeric;

@Getter
public class Operation {
    private static final List<String> validFields = List.of("id", "title", "content", "views", "timestamp");
    private static final List<String> integerFields = List.of("views", "timestamp");
    private final Operator operator;
    private final List<String> parameters;

    public Operation(String operator, List<String> parameters) {
        this.operator = Operator.valueOf(operator);
        this.parameters = parameters;
        validateNumberOfParameters();
        validateFieldNames();
        validateOperationWithNumericFields();
        validateDoubleQuotesInTextField();
    }

    private void validateNumberOfParameters() {
        if (operator.getNumberParameters() != parameters.size()) {
            throw new InvalidParameterException(format("Operator %s should have %d parameter(s)", operator.name(),
                    operator.getNumberParameters()));
        }
    }

    private void validateFieldNames() {
        if (operator == EQUAL || operator == GREATER_THAN || operator == LESS_THAN) {
            if (!validFields.contains(parameters.get(0))) {
                throw new InvalidParameterException(format("%s is invalid field", parameters.get(0)));
            }
        }
    }

    private void validateOperationWithNumericFields() {
        if (operator == GREATER_THAN || operator == LESS_THAN) {
            if (!integerFields.contains(parameters.get(0))) {
                throw new InvalidParameterException(format("Field %s is not of integer data type", parameters.get(0)));
            }
            if (!isNumeric(parameters.get(1))) {
                throw new InvalidParameterException(format("Value %s is not of valid integer value", parameters.get(1)));
            }
        }
    }

    private void validateDoubleQuotesInTextField() {
        if (operator == EQUAL && !integerFields.contains(parameters.get(0))) {
            if (!parameters.get(1).matches("\"(.*)\"")) {
                throw new InvalidParameterException(format("Parameter %s value should be enclosed with double quotes",
                        parameters.get(0)));
            }
        }
    }
}