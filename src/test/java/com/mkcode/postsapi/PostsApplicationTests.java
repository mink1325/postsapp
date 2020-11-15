package com.mkcode.postsapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mkcode.postsapi.persistence.StoreRepository;
import com.mkcode.postsapi.persistence.model.Post;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostsApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StoreRepository storeRepository;

    @BeforeAll
    void setup() {
        IntStream.rangeClosed(1, 10)
                .mapToObj(this::createPost)
                .forEach(storeRepository::save);
    }

    @Test
    void testSavePost() throws Exception {
        assertThat(storeRepository.existsById("first-post")).isFalse();
        assertThat(storeRepository.count()).isEqualTo(10);
        mockMvc.perform(post("/store")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "id": "first-post",
                        "title": "My First Post",
                        "content": "Hello World!",
                        "views": 111,
                        "timestamp": 1555832341
                        }
                        """))
                .andExpect(status().isOk());

        assertThat(storeRepository.existsById("first-post")).isTrue();
        assertThat(storeRepository.count()).isEqualTo(11);
    }

    @Test
    void testInvalidSave() throws Exception {
        mockMvc.perform(post("/store")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "title": "My First Post",
                        "content": "Hello World!",
                        "views": 111,
                        "timestamp": 1555832341
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(notNullValue()));
    }

    @Test
    void testQuery() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(get("/store")
                .param("query", "EQUAL(id, \"id1\")"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(createPost(1)))));
    }

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
            "EQUAL(id,\"100\");0",
            "OR(EQUAL(title,\"title 1\"),EQUAL(content,\"text 2\"));2",
            "LESS_THAN(views,4);3",
            "AND(GREATER_THAN(views,1),LESS_THAN(views,4));2",
            "NOT(GREATER_THAN(views,4));4"
    })
    void testQuery(String query, int expectedResults) throws Exception {
        mockMvc.perform(get("/store")
                .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedResults)));
    }

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
            "EQUAL(2);Operator EQUAL should have 2 parameter(s)",
            "INVALID(2;Expression 'INVALID(2' is not valid"})
    void testInvalidQuery(String query, String expectedMessage) throws Exception {
        mockMvc.perform(get("/store")
                .param("query", query))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedMessage));
    }

    private Post createPost(int i) {
        return new Post("id" + i, "title " + i, "text " + i, i, 10000 + i);
    }
}
