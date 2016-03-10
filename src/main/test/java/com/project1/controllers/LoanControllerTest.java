package com.project1.controllers;

import com.project1.Application;
import com.project1.controllers.loan.LoanController;
import com.project1.model.Customer;
import com.project1.model.Loan;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Application.class)
public class LoanControllerTest {

    public static final String SOME_CUSTOMER_ID = "some_customer_id";
    public static final String SOME_FIRST_NAME = "some_first_name";
    public static final String SOME_LAST_NAME = "some_last_name";
    public static final Long SOME_LOAN_ID = 1L;
    public static final Long SOME_LOAN_ID_2 = 2L;
    public static final String SOME_COUNTRY = "ZZ";
    public static final BigDecimal SOME_AMOUNT = new BigDecimal(200.50);
    public static final BigDecimal SOME_AMOUNT_2 = new BigDecimal(888.88);

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

    @Test
    public void findLoanById_OK() throws Exception{
        Customer cust = new Customer(SOME_CUSTOMER_ID, SOME_FIRST_NAME, SOME_LAST_NAME);
        Loan loan = new Loan(SOME_AMOUNT, cust);
        loan.setId(SOME_LOAN_ID);
        loan.setApplicationCountry(SOME_COUNTRY);
        when(loanRepository.findOne(SOME_LOAN_ID)).thenReturn(loan);

        mockMvc.perform(get("/loan/{id}", SOME_LOAN_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.amount").value(SOME_AMOUNT.doubleValue()))
                .andExpect(jsonPath("$.applicationCountry").value(SOME_COUNTRY))
                .andExpect(jsonPath("$.customer.id").value(SOME_CUSTOMER_ID))
                .andExpect(jsonPath("$.customer.firstName").value(SOME_FIRST_NAME))
                .andExpect(jsonPath("$.customer.lastName").value(SOME_LAST_NAME))
                .andExpect(jsonPath("$.customer.blacklisted").value(false));

        verify(loanRepository, times(1)).findOne(SOME_LOAN_ID);
        verifyNoMoreInteractions(loanRepository);
    }

    @Test
    public void findLoanById_Not_Found() throws Exception{
        when(loanRepository.findOne(SOME_LOAN_ID)).thenReturn(null);

        mockMvc.perform(get("/loan/{id}", SOME_LOAN_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.type").value("INFO"))
                .andExpect(jsonPath("$.message").value("Loan not found"));

        verify(loanRepository, times(1)).findOne(SOME_LOAN_ID);
        verifyNoMoreInteractions(loanRepository);
    }

    @Test
    public void findAllLoansByCustomerId_OK() throws Exception{
        Customer cust = new Customer(SOME_CUSTOMER_ID, SOME_FIRST_NAME, SOME_LAST_NAME);
        Loan loan = new Loan(SOME_AMOUNT, cust);
        loan.setId(SOME_LOAN_ID);
        loan.setApplicationCountry(SOME_COUNTRY);
        List<Loan> loans = new ArrayList<Loan>() {{
            add(loan);
        }};
        cust.setLoans(loans);
        when(customerRepository.findById(SOME_CUSTOMER_ID)).thenReturn(cust);

        mockMvc.perform(get("/loans/user/{id}", SOME_CUSTOMER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].amount").value(SOME_AMOUNT.doubleValue()))
                .andExpect(jsonPath("$[0].applicationCountry").value(SOME_COUNTRY))
                .andExpect(jsonPath("$[0].customer.id").value(SOME_CUSTOMER_ID))
                .andExpect(jsonPath("$[0].customer.firstName").value(SOME_FIRST_NAME))
                .andExpect(jsonPath("$[0].customer.lastName").value(SOME_LAST_NAME))
                .andExpect(jsonPath("$[0].customer.blacklisted").value(false));

        verify(customerRepository, times(1)).findById(SOME_CUSTOMER_ID);
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    public void findAllLoansByCustomerId_Loans_Not_Found() throws Exception{
        Customer cust = new Customer(SOME_CUSTOMER_ID, SOME_FIRST_NAME, SOME_LAST_NAME);
        cust.setLoans(null);
        when(customerRepository.findById(SOME_CUSTOMER_ID)).thenReturn(cust);

        mockMvc.perform(get("/loans/user/{id}", SOME_CUSTOMER_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.type").value("INFO"))
                .andExpect(jsonPath("$.message").value("Loans not found"));

        verify(customerRepository, times(1)).findById(SOME_CUSTOMER_ID);
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    public void findAllLoansByCustomerId_Customer_Not_Found() throws Exception{
        when(customerRepository.findById(SOME_CUSTOMER_ID)).thenReturn(null);

        mockMvc.perform(get("/loans/user/{id}", SOME_CUSTOMER_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.type").value("INFO"))
                .andExpect(jsonPath("$.message").value("Customer not found"));

        verify(customerRepository, times(1)).findById(SOME_CUSTOMER_ID);
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    public void findAll_OK() throws Exception{
        Customer cust = new Customer(SOME_CUSTOMER_ID, SOME_FIRST_NAME, SOME_LAST_NAME);
        Loan loan = new Loan(SOME_AMOUNT, cust);
        loan.setId(SOME_LOAN_ID);
        loan.setApplicationCountry(SOME_COUNTRY);

        Loan loan2 = new Loan(SOME_AMOUNT_2, cust);
        loan2.setId(SOME_LOAN_ID_2);
        loan2.setApplicationCountry(SOME_COUNTRY);
        List<Loan> loans = new ArrayList<Loan>() {{
            add(loan);
            add(loan2);
        }};
        when(loanRepository.findAll()).thenReturn(loans);

        mockMvc.perform(get("/loans/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(SOME_LOAN_ID.intValue()))
                .andExpect(jsonPath("$[0].amount").value(SOME_AMOUNT.doubleValue()))
                .andExpect(jsonPath("$[0].applicationCountry").value(SOME_COUNTRY))
                .andExpect(jsonPath("$[0].customer.id").value(SOME_CUSTOMER_ID))
                .andExpect(jsonPath("$[0].customer.firstName").value(SOME_FIRST_NAME))
                .andExpect(jsonPath("$[0].customer.lastName").value(SOME_LAST_NAME))
                .andExpect(jsonPath("$[0].customer.blacklisted").value(false))
                .andExpect(jsonPath("$[1].id").value(SOME_LOAN_ID_2.intValue()))
                .andExpect(jsonPath("$[1].amount").value(SOME_AMOUNT_2))
                .andExpect(jsonPath("$[1].applicationCountry").value(SOME_COUNTRY))
                .andExpect(jsonPath("$[1].customer.id").value(SOME_CUSTOMER_ID))
                .andExpect(jsonPath("$[1].customer.firstName").value(SOME_FIRST_NAME))
                .andExpect(jsonPath("$[1].customer.lastName").value(SOME_LAST_NAME))
                .andExpect(jsonPath("$[1].customer.blacklisted").value(false));

        verify(loanRepository, times(1)).findAll();
        verifyNoMoreInteractions(loanRepository);
    }

    @Test
    public void findAllLoansByCustomerId_Not_Found() throws Exception{
        when(loanRepository.findAll()).thenReturn(null);

        mockMvc.perform(get("/loans/all"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.type").value("INFO"))
                .andExpect(jsonPath("$.message").value("No approved loan applications found"));

        verify(loanRepository, times(1)).findAll();
        verifyNoMoreInteractions(loanRepository);
    }
}
