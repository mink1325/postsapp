package com.mkcode.postsapi.service;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toUnmodifiableList;

class ExpressionParser {
    private static final Pattern pattern = Pattern.compile("^(EQUAL|AND|OR|NOT|GREATER_THAN|LESS_THAN)\\((.*\\S.*)\\)$");

    static Operation parseOperation(String expression) {
        Matcher matcher = pattern.matcher(expression);
        if (matcher.find()) {
            var operation = matcher.group(1);
            var parameterString = matcher.group(2);
            return new Operation(operation, extractParameters(parameterString));
        } else {
            throw new ExpressionValidationException(expression);
        }
    }

    private static List<String> extractParameters(String parameterString) {
        int openBrackets = 0;
        boolean openDoubleQuotes = false;
        int splitter = 0;
        List<String> parameters = new LinkedList<>();
        for (int i = 0; i < parameterString.length(); i++) {
            switch (parameterString.charAt(i)) {
                case '(' -> openBrackets++;
                case ')' -> openBrackets--;
                case '"' -> openDoubleQuotes = !openDoubleQuotes;
            }
            if (parameterString.charAt(i) == ',' && openBrackets == 0 && !openDoubleQuotes) {
                parameters.add(parameterString.substring(splitter, i));
                splitter = i + 1;
            }
        }
        parameters.add(parameterString.substring(splitter));
        return parameters.stream().map(String::strip).collect(toUnmodifiableList());
    }
}
