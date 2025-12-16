package com.example.springboot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.cloud.firestore.Firestore;

@SpringBootTest(classes = AppConfig.class)
public class AppConfigTest {

    // Mock Firebase dependencies to prevent startup errors during testing
    // without a real service-account.json file.
    @MockBean
    private FirebaseApp firebaseApp;

    @MockBean
    private FirebaseAuth firebaseAuth;

    @MockBean
    private Firestore firestore;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void testObjectMapperConfiguration() {
        // Positive Test: Ensure ObjectMapper is configured correctly for Dates
        Assertions.assertNotNull(objectMapper);
        Assertions.assertFalse(objectMapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS), 
            "WRITE_DATES_AS_TIMESTAMPS should be disabled");
    }

    @Test
    public void testRestTemplateBeanCreation() {
        // Positive Test: Ensure RestTemplate bean is created
        Assertions.assertNotNull(restTemplate);
    }
}