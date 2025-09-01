package com.example.PollApp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PollAppApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void pollAppScenario() throws Exception {
	// 1. Create User 1
	mockMvc.perform(post("/users")
		.contentType(MediaType.APPLICATION_JSON)
		.content("{\"id\":1,\"name\":\"Alice\",\"username\":\"alice\",\"email\":\"alice@example.com\"}"))
		.andExpect(status().isOk());

	// 2. List all users (should show Alice)
	mockMvc.perform(get("/users"))
		.andExpect(status().isOk())
		.andExpect(content().string(org.hamcrest.Matchers.containsString("Alice")));

	// 3. Create User 2
	mockMvc.perform(post("/users")
		.contentType(MediaType.APPLICATION_JSON)
		.content("{\"id\":2,\"name\":\"Bob\",\"username\":\"bob\",\"email\":\"bob@example.com\"}"))
		.andExpect(status().isOk());

	// 4. List all users (should show Alice and Bob)
	mockMvc.perform(get("/users"))
		.andExpect(status().isOk())
		.andExpect(content().string(org.hamcrest.Matchers.containsString("Alice")))
		.andExpect(content().string(org.hamcrest.Matchers.containsString("Bob")));

	// 5. User 1 creates a poll
	mockMvc.perform(post("/polls")
		.contentType(MediaType.APPLICATION_JSON)
		.content("{\"id\":1,\"question\":\"Favorite color?\",\"creatorId\":1,\"voteOptions\":[{\"id\":1,\"caption\":\"Red\",\"presentationOrder\":1},{\"id\":2,\"caption\":\"Blue\",\"presentationOrder\":2}]}"))
		.andExpect(status().isOk());

	// 6. List polls (should show the new poll)
	mockMvc.perform(get("/polls"))
		.andExpect(status().isOk())
		.andExpect(content().string(org.hamcrest.Matchers.containsString("Favorite color?")));

	// 7. User 2 votes on the poll (for Red)
	mockMvc.perform(post("/votes")
		.contentType(MediaType.APPLICATION_JSON)
		.content("{\"id\":1,\"userId\":2,\"voteOptionId\":1}"))
		.andExpect(status().isOk());

	// 8. User 2 changes vote (to Blue)
	mockMvc.perform(put("/votes/1")
		.contentType(MediaType.APPLICATION_JSON)
		.content("{\"id\":1,\"userId\":2,\"voteOptionId\":2}"))
		.andExpect(status().isOk());

	// 9. List votes (should show vote for Blue)
	mockMvc.perform(get("/votes"))
		.andExpect(status().isOk())
		.andExpect(content().string(org.hamcrest.Matchers.containsString("voteOptionId\":2")));

	// 10. Delete the poll
	mockMvc.perform(delete("/polls/1"))
		.andExpect(status().isOk());

	// 11. List votes (should be empty)
	mockMvc.perform(get("/votes"))
		.andExpect(status().isOk())
		.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("voteOptionId"))));
    }
}
