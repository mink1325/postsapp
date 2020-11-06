package com.mkcode.postsapi.service;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.mkcode.postsapi.service.OperationParser.parseOperation;
import static org.assertj.core.api.Assertions.assertThat;

class OperationParserTest {

    @Test
    void testResolveExpression(){
        assertThat(parseOperation("EQUAL(id, \"first-post\")"))
                .extracting(Pair::getKey, Pair::getValue)
                .containsExactly("EQUAL", List.of("id", "first-post"));
        assertThat(parseOperation("EQUAL(views,100)"))
                .extracting(Pair::getKey, Pair::getValue)
                .containsExactly("EQUAL", List.of("views", "100"));
    }

}