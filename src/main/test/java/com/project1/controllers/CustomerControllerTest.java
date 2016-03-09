package com.project1.controllers;

import com.project1.Application;
import com.project1.controllers.customer.CustomerController;
import com.project1.model.Customer;
import com.project1.repositories.CustomerRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Application.class)
public class CustomerControllerTest {

    public static final String SOME_ID = "some_id";
    public static final String SOME_FIRST_NAME = "some_first_name";
    public static final String SOME_LAST_NAME = "some_last_name";

    private MockMvc mockMvc;

    @InjectMocks
    CustomerController customerController;

    @Mock
    CustomerRepository customerRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
    }

    @Test
    public void findCustomerById_OK() throws Exception{
        Customer cust = new Customer(SOME_ID, SOME_FIRST_NAME, SOME_LAST_NAME);
        when(customerRepository.findById(SOME_ID)).thenReturn(cust);

        mockMvc.perform(get("/customer/{id}", SOME_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(SOME_ID))
                .andExpect(jsonPath("$.firstName").value(SOME_FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(SOME_LAST_NAME));

        verify(customerRepository, times(1)).findById(SOME_ID);
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    public void findCustomerById_Not_Found() throws Exception{
        when(customerRepository.findById(SOME_ID)).thenReturn(null);

        mockMvc.perform(get("/customer/{id}", SOME_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.type").value("INFO"))
                .andExpect(jsonPath("$.message").value("User not found"));

        verify(customerRepository, times(1)).findById(SOME_ID);
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    public void blacklistCustomer_OK() throws Exception{
        Customer cust = new Customer(SOME_ID, SOME_FIRST_NAME, SOME_LAST_NAME);
        when(customerRepository.findById(SOME_ID)).thenReturn(cust);

        mockMvc.perform(put("/customer/blacklist/{id}", SOME_ID))
                .andExpect(status().isOk());
    }

    @Test
    public void blacklistCustomer_Not_Found() throws Exception{
        when(customerRepository.findById(SOME_ID)).thenReturn(null);

        mockMvc.perform(put("/customer/blacklist/{id}", SOME_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.type").value("INFO"))
                .andExpect(jsonPath("$.message").value("Customer not found"));;
    }

    @Test
    public void blacklistCustomer_Already_Blacklisted() throws Exception{
        Customer cust = new Customer(SOME_ID, SOME_FIRST_NAME, SOME_LAST_NAME);
        cust.setBlacklisted(true);
        when(customerRepository.findById(SOME_ID)).thenReturn(cust);

        mockMvc.perform(put("/customer/blacklist/{id}", SOME_ID))
                .andExpect(status().isAlreadyReported())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.type").value("INFO"))
                .andExpect(jsonPath("$.message").value("User already blacklisted"));;
    }
}

