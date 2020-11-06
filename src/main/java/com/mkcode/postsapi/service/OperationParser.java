package com.mkcode.postsapi.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

class OperationParser {
    private static final Pattern pattern = Pattern.compile("^(EQUAL|AND|OR|NOT|GREATER_THAN|LESS_THAN)\\((.*)\\)$");

    static Pair<String, List<String>> parseOperation(String expression) {
        Matcher matcher = pattern.matcher(expression);
        if (matcher.find()) {
            var operandsStr = matcher.group(2);
            int openBrackets = 0;
            int splitter = 0;
            int i = 0;
            while (i < operandsStr.length()) {
                if (operandsStr.charAt(i) == '(') openBrackets++;
                if (operandsStr.charAt(i) == ')') openBrackets--;
                if (operandsStr.charAt(i) == ',' && openBrackets == 0) {
                    splitter = i;
                    break;
                }
                i++;
            }
            if (splitter != 0) {
                return Pair.of(matcher.group(1), List.of(StringUtils.strip(operandsStr.substring(0, splitter), " \""),
                        StringUtils.strip(operandsStr.substring(splitter+1), " \"")));
            } else
                return Pair.of(matcher.group(1), List.of(StringUtils.strip(operandsStr, " \"")));
        } else {
            throw new RuntimeException(format("expression '%s' is not valid", expression));
        }
    }
}
