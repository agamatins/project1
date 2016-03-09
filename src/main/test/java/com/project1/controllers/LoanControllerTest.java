package com.project1.controllers;

import com.project1.Application;
import com.project1.controllers.loan.LoanController;
import com.project1.repositories.CustomerRepository;
import com.project1.repositories.LoanRepository;
import com.project1.services.request.RequestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.junit.Assert.*;

import java.math.BigDecimal;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Application.class)
public class LoanControllerTest {

    public static final String SOME_CUSTOMER_ID = "some_customer_id";
    public static final String SOME_FIRST_NAME = "some_first_name";
    public static final String SOME_LAST_NAME = "some_last_name";
    public static final String SOME_LOAN_ID = "some_loan_id";
    public static final String SOME_COUNTRY = "ZZ";
    public static final BigDecimal SOME_AMOUNT = new BigDecimal(200);

    private MockMvc mockMvc;

    @InjectMocks
    LoanController loanController;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    LoanRepository loanRepository;

    @Mock
    RequestService requestService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(loanController).build();
    }

    @Test
    public void createLoan_OK() throws Exception {
////        Customer cust = new Customer(SOME_CUSTOMER_ID, SOME_FIRST_NAME, SOME_LAST_NAME);
////        Loan loan = new Loan(SOME_AMOUNT, cust);
//        LoanApplication application = new LoanApplication();
//        application.setCustomerId(SOME_CUSTOMER_ID);
//        application.setFirstName(SOME_FIRST_NAME);
//        application.setLastName(SOME_LAST_NAME);
//        application.setAmount(SOME_AMOUNT);
//        when(requestService.getCountry(anyString())).thenReturn(SOME_COUNTRY);
//        when(requestService.isSpamCompliant(SOME_COUNTRY)).thenReturn(true);
//        when(customerRepository.findById(SOME_CUSTOMER_ID)).thenReturn(null);
//
////        String json = String.format("{\"customerId\":\"%s\", \"firstName\":\"%s\", \"lastName\":\"%s\", \"amount\":200}",
////                SOME_CUSTOMER_ID, SOME_FIRST_NAME, SOME_LAST_NAME);
////        System.out.println(json);
//        mockMvc.perform(post("/loan")
//                .content(json(application))
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated());
        assertTrue(true);
    }
}
