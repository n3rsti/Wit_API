package com.web.wit;


import com.web.wit.user.User;
import com.web.wit.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectWriter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

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


    /* Utility function for creating new User.
    * Simple POST request.
    * This method expects HTTP 201 so any other response is considered as a failure
    * and will result in thrown Exception.
    */
    void createUser(User user) throws Exception{
        /* Convert to JSON */
        String json = objectToJson(user);

        /* Send POST request to create user */
        MvcResult result = this.mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)).andReturn();

        int resultStatus = result.getResponse().getStatus();
        if(resultStatus != 201){
            throw new Exception("User could not be created. Response code: " + resultStatus);
        }
    }

    String objectToJson(Object obj) throws Exception{
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        return ow.writeValueAsString(obj);
    }
}
