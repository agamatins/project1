package com.project1.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project1.Application;
import com.project1.controllers.loan.LoanController;
import com.project1.model.Customer;
import com.project1.model.Loan;
import com.project1.model.LoanApplication;
import com.project1.repositories.CustomerRepository;
import com.project1.repositories.LoanRepository;
import com.project1.services.request.RequestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Application.class)
public class LoanControllerIntegrationTest {
    public static final String SOME_CUSTOMER_ID = "some_customer_id";
    public static final String SOME_WRONG_CUSTOMER_ID = "some_wrong_customer_id";
    public static final String SOME_FIRST_NAME = "somefirstname";
    public static final String SOME_LAST_NAME = "somelastname";
    public static final String SOME_WRONG_FIRST_NAME = "somewrongfirstname";
    public static final String SOME_WRONG_LAST_NAME = "somewronglastname";
    public static final BigDecimal SOME_AMOUNT = new BigDecimal(200.50);

    private MockMvc mockMvc;

    @Autowired
    LoanController loanController;

    @Autowired
    CustomerRepository customerRepository;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(loanController).build();
    }

    @Test
    public void createLoan_OK_NewUser() throws Exception {
        LoanApplication application = new LoanApplication();
        application.setCustomerId(SOME_CUSTOMER_ID);
        application.setFirstName(SOME_FIRST_NAME);
        application.setLastName(SOME_LAST_NAME);
        application.setAmount(SOME_AMOUNT);

        mockMvc.perform(post("/loan")
                .content(asJsonString(application))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/loan/{id}", 1)
                .content(asJsonString(application))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.amount").value(SOME_AMOUNT.doubleValue()))
                .andExpect(jsonPath("$.customer.id").value(SOME_CUSTOMER_ID))
                .andExpect(jsonPath("$.customer.firstName").value(SOME_FIRST_NAME))
                .andExpect(jsonPath("$.customer.lastName").value(SOME_LAST_NAME))
                .andExpect(jsonPath("$.customer.blacklisted").value(false));

        mockMvc.perform(get("/loans/user/{id}", SOME_CUSTOMER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].amount").value(SOME_AMOUNT.doubleValue()))
                .andExpect(jsonPath("$[0].customer.id").value(SOME_CUSTOMER_ID))
                .andExpect(jsonPath("$[0].customer.firstName").value(SOME_FIRST_NAME))
                .andExpect(jsonPath("$[0].customer.lastName").value(SOME_LAST_NAME))
                .andExpect(jsonPath("$[0].customer.blacklisted").value(false));

        mockMvc.perform(get("/loans/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].amount").value(SOME_AMOUNT.doubleValue()))
                .andExpect(jsonPath("$[0].customer.id").value(SOME_CUSTOMER_ID))
                .andExpect(jsonPath("$[0].customer.firstName").value(SOME_FIRST_NAME))
                .andExpect(jsonPath("$[0].customer.lastName").value(SOME_LAST_NAME))
                .andExpect(jsonPath("$[0].customer.blacklisted").value(false));

        //blacklist user
        Customer cust = new Customer(SOME_CUSTOMER_ID, SOME_FIRST_NAME, SOME_LAST_NAME);
        cust.setBlacklisted(true);
        customerRepository.save(cust);

        mockMvc.perform(post("/loan")
                .content(asJsonString(application))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isExpectationFailed());
        //expect no changes
        mockMvc.perform(get("/loans/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)));
        //avoid spam fail
        try {
            Thread.sleep(1001);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //conflicted user
        cust = new Customer(SOME_CUSTOMER_ID, SOME_WRONG_FIRST_NAME, SOME_WRONG_LAST_NAME);
        customerRepository.save(cust);

        mockMvc.perform(post("/loan")
                .content(asJsonString(application))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
        //expect no changes
        mockMvc.perform(get("/loans/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)));

        //another user
        cust = new Customer(SOME_CUSTOMER_ID, SOME_FIRST_NAME, SOME_LAST_NAME);
        customerRepository.save(cust);

        application = new LoanApplication();
        application.setCustomerId(SOME_WRONG_CUSTOMER_ID);
        application.setFirstName(SOME_WRONG_FIRST_NAME);
        application.setLastName(SOME_WRONG_LAST_NAME);
        application.setAmount(SOME_AMOUNT);

        mockMvc.perform(post("/loan")
                .content(asJsonString(application))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/loan/{id}", 1)
                .content(asJsonString(application))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.amount").value(SOME_AMOUNT.doubleValue()))
                .andExpect(jsonPath("$.customer.id").value(SOME_CUSTOMER_ID))
                .andExpect(jsonPath("$.customer.firstName").value(SOME_FIRST_NAME))
                .andExpect(jsonPath("$.customer.lastName").value(SOME_LAST_NAME))
                .andExpect(jsonPath("$.customer.blacklisted").value(false));

        mockMvc.perform(get("/loan/{id}", 2)
                .content(asJsonString(application))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.amount").value(SOME_AMOUNT.doubleValue()))
                .andExpect(jsonPath("$.customer.id").value(SOME_WRONG_CUSTOMER_ID))
                .andExpect(jsonPath("$.customer.firstName").value(SOME_WRONG_FIRST_NAME))
                .andExpect(jsonPath("$.customer.lastName").value(SOME_WRONG_LAST_NAME))
                .andExpect(jsonPath("$.customer.blacklisted").value(false));

        mockMvc.perform(get("/loans/user/{id}", SOME_CUSTOMER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].amount").value(SOME_AMOUNT.doubleValue()))
                .andExpect(jsonPath("$[0].customer.id").value(SOME_CUSTOMER_ID))
                .andExpect(jsonPath("$[0].customer.firstName").value(SOME_FIRST_NAME))
                .andExpect(jsonPath("$[0].customer.lastName").value(SOME_LAST_NAME))
                .andExpect(jsonPath("$[0].customer.blacklisted").value(false));

        mockMvc.perform(get("/loans/user/{id}", SOME_WRONG_CUSTOMER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].amount").value(SOME_AMOUNT.doubleValue()))
                .andExpect(jsonPath("$[0].customer.id").value(SOME_WRONG_CUSTOMER_ID))
                .andExpect(jsonPath("$[0].customer.firstName").value(SOME_WRONG_FIRST_NAME))
                .andExpect(jsonPath("$[0].customer.lastName").value(SOME_WRONG_LAST_NAME))
                .andExpect(jsonPath("$[0].customer.blacklisted").value(false));

        mockMvc.perform(get("/loans/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(2)));

    }


    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
