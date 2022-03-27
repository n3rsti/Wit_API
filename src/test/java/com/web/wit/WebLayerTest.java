package com.web.wit;


import com.web.wit.user.User;
import com.web.wit.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectWriter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    void createUserEndpointShouldReturnResponseInJsonFormat() throws Exception {
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

}
