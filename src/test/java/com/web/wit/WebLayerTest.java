package com.web.wit;


import com.web.wit.user.User;
import com.web.wit.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectWriter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"spring.data.mongodb.database=wit_api_tests"})
@AutoConfigureMockMvc
public class WebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    // userRepository is used only to clean up database after every test
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void getUsersEndpointShouldReturnHTTP200StatusAndJsonTypeResponse() throws Exception {
        this.mockMvc.perform(get("/api/v1/users/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void createUserEndpointShouldReturn201WithValidData() throws Exception {
        User user = new User("user");

        // convert to JSON
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(user);

        this.mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated());
    }

    @Test
    void createUserEndpointShouldReturn409WhenDuplicatedUserIsCreated() throws Exception {
        User user = new User("user");

        // convert to JSON
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(user);

        /*
         * First request is creating user with unique data which wasn't used before,
         * therefore it is valid and should return 201 Created. We test response code of first request just in case something
         * goes wrong, even though this request isn't the subject of our test.
         * */
        this.mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        /*
         * Second request is creating user with unique data which was used before (user created in previous request),
         * therefore it is not valid and should return 409 Conflict.
         * */
        this.mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(status().isConflict());
    }

    /* Valid headers:
     * Content-Type: application/json
     * */
    @Test
    void createUserEndpointShouldReturnValidHeaders() throws Exception {
        User user = new User("user");

        // convert to JSON
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(user);


        this.mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(
                        content().contentType(MediaType.APPLICATION_JSON)
                );
    }

    @Test
    void getUserByIdEndpointWithValidInputShouldReturn200AndReturnUserData() throws Exception {
        String userId = "userId";
        String username = "user";

        User user = new User("user");
        user.setId(userId);

        // convert to JSON
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(user);

        /*
         * First request is creating user with unique data which wasn't used before,
         * therefore it is valid and should return 201 Created. We test response code of first request just in case something
         * goes wrong, even though this request isn't the subject of our test.
         * */
        this.mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        /*
         * This request is getting data of user created in previous request. It is valid and should return 200 OK.
         */

        MvcResult result = this.mockMvc.perform(get("/api/v1/users/" + userId))
                .andExpect(status().isOk())
                .andReturn();

        /*
         * Check if response body contains user data.
         * In this test we only check if response contains username
         */
        boolean contentContainsUsername = result.getResponse().getContentAsString().contains(username);

        Assertions.assertTrue(contentContainsUsername);
    }

    @Test
    void updateUserShouldReturnBadRequestWhenUserTriesToModifyId() throws Exception {
        String userId = "userId";
        String username = "user";

        User user = new User(username);
        user.setId(userId);

        // convert to JSON
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(user);

        /*
         * First request is creating user with unique data which wasn't used before,
         * therefore it is valid and should return 201 Created. We test response code of first request just in case something
         * goes wrong, even though this request isn't the subject of our test.
         * */
        this.mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());


        /* Create new JSON with updated ID */
        String newUserId = "changedUserId";
        user.setId(newUserId);
        json = ow.writeValueAsString(user);


        /* Send PUT request on /api/v1/user/oldId
         * with new ID in body which results in attempt to change ID
         *  */
        this.mockMvc.perform(put("/api/v1/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(status().isBadRequest());
    }

    /* Simple test request. In this test we try to update not existing user, so
     * we should receive HTTP 404 Not Found.
     * */
    @Test
    void updateUserShouldReturnHTTPNotFoundWhenUserWithIdFromURIDoesntExist() throws Exception {
        String testId = "notExistingId";
        this.mockMvc.perform(put("/api/v1/users/" + testId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        ).andExpect(status().isNotFound());
    }
}
