package com.ecommerce.customer.controller;

import com.ecommerce.customer.dto.CustomerDto;
import com.ecommerce.customer.dto.CustomerRequest;
import com.ecommerce.customer.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CustomerControllerTest {
    private MockMvc mockMvc;

    @Mock
    private CustomerService service;

    @InjectMocks
    private CustomerController controller;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void register_success() throws Exception {
        CustomerRequest req = CustomerRequest.builder().firstName("John").lastName("Doe").email("j@d.com").build();
        CustomerDto dto = CustomerDto.builder().id(1L).email("j@d.com").build();
        when(service.register(any())).thenReturn(dto);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }
}
