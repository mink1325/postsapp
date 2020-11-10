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
	void setup(){
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
	void testInvalidSave(){
//todo
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
	@CsvSource(value = {"EQUAL(id,100);0", "LESS_THAN(views,4);3"}, delimiter = ';')
	void testQuery(String query, int expectedResults) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		mockMvc.perform(get("/store")
				.param("query", query))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(expectedResults)));
	}

	@Test
	void testInvalidQuery(){
//todo
	}

	private Post createPost(int i) {
		return new Post("id" + i, "title "+ i, "text " + i, i, 10000 + i);
	}
}
