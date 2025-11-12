package com.delivery.optimizer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// HADO HOMA L IMPORTS L MOHIMIN
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * test endpoint: POST /api/customers
     */
    @Test
    public void testCreateCustomerAPI() throws Exception {

        // 1. L JSON li ghadi nsefto (b7al f Insomnia)
        // T2ekkedna men l validation dial Customer ma talbach "preferredTimeSlot" darori
        String customerJson = "{\"name\":\"Test Customer JUnit\",\"address\":\"123 Test St\",\"latitude\":31.0,\"longitude\":-8.0, \"preferredTimeSlot\":\"09:00-12:00\"}";

        // 2. Kan-testiw l POST /api/customers
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))


                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Customer JUnit"));
    }
}