package com.project1.controllers.loan;

import com.project1.model.Customer;
import com.project1.model.Loan;
import com.project1.model.LoanApplication;
import com.project1.model.Message;
import com.project1.repositories.CustomerRepository;
import com.project1.repositories.LoanRepository;
import com.project1.services.request.RequestService;
import com.project1.utils.AppDefaults;
import com.project1.utils.MessageType;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
public class LoanController {
    final static Logger logger = LogManager.getLogger(LoanController.class);

    @Autowired
    RequestService requestService;
    @Autowired
    LoanRepository loanRepository;
    @Autowired
    CustomerRepository customerRepository;

    @RequestMapping(value = "/loan", method = RequestMethod.POST)
    public ResponseEntity<?> createLoan(@Valid @RequestBody LoanApplication application, UriComponentsBuilder ucBuilder, HttpServletRequest request) {
        logger.info("Creating New Loan Application");

        logger.info("Request ip " + request.getRemoteAddr());
        String country = ((request.getRemoteAddr() != null) ? requestService.getCountry(request.getRemoteAddr()) : AppDefaults.DEFAULT_COUNTRY);
        //request spamming from country check
        if (!requestService.isSpamCompliant(country)){
            logger.info("Rejecting, Too many requests from " + country);
            return new ResponseEntity<>(new Message(MessageType.ERROR, "Rejected: Too many requests from " + country), HttpStatus.TOO_MANY_REQUESTS);
        }
        //check customer
        String customerId = application.getCustomerId();
        Customer customer = customerRepository.findById(customerId);
        if (customer != null){
            //okwe got you in the db.
            //check is data in application matching data in the db. we can't allow users with the same id have different names
            if (!isUserDataCorrect(customer, application)){
                logger.info("User data conflict: wrong mane and surname mentioned for customer id=" + customerId);
                return new ResponseEntity<>(new Message(MessageType.ERROR, "Rejected: Wrong personal data for given user"), HttpStatus.CONFLICT);
            }

            //check if user is blacklisted. we don't deal with bad men. at lest he's been blacklisted for a reason! or not?
            if (customer.isBlacklisted()){
                logger.info(String.format("Rejected as user id=%s is blacklisted", customerId));
                return new ResponseEntity<>(new Message(MessageType.ERROR, "Rejected: customer is blacklisted"), HttpStatus.EXPECTATION_FAILED);
            }

        } else {
            //we don't have user with this id in our database. let's create one.
            customer = new Customer(customerId, application.getFirstName(), application.getLastName());
        }
        customerRepository.save(customer);
        logger.info("Customer created " + customer.getId());

        //now we can create loan application
        Loan loan = new Loan(application.getAmount(), customer);
        loan.setApplicationCountry(country);
        loanRepository.save(loan);
        logger.info("Loan application created " + loan.getId());
        //could be nice to include url for a newly created loan to header
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/loan/{id}").buildAndExpand(loan.getId()).toUri());
        return new ResponseEntity<Void>(null, headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/loan/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findLoanById(@PathVariable("id") long id) {
        logger.info("Finding Loan application " + id);
        Loan loan = loanRepository.findOne(id);

        if (loan == null){
            logger.info(String.format("Loan with id-%s cannot be found", id));
            return new ResponseEntity<>(new Message(MessageType.INFO, "Loan not found"), HttpStatus.NOT_FOUND);
        }
        logger.info(String.format("Found Loan Application %s %s %s", loan.getId(), loan.getCustomer().getId(), loan.getAmount().toString()));
        return new ResponseEntity<>(loan, HttpStatus.OK);
    }

    @RequestMapping(value = "/loans/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAllLoans() {
        logger.info("Finding All Approved Loan Applications");
        Iterable<Loan> loans = loanRepository.findAll();

        if (loans == null){
            logger.info("No approved loan applications found");
            return new ResponseEntity<>(new Message(MessageType.INFO, "No approved loan applications found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(loans, HttpStatus.OK);
    }

    @RequestMapping(value = "/loans/user/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAllLoansForUser(@PathVariable("id") String id) {
        logger.info("Finding All Approved Loan Applications For User " + id);
        Customer customer = customerRepository.findById(id);

        //do we have user with this id?
        if (customer == null){
            logger.info(String.format("Customer with id %s not found", id));
            return new ResponseEntity<>(new Message(MessageType.INFO, "Customer not found"), HttpStatus.NOT_FOUND);
        }

        //does this user have any loan applications?
        if (CollectionUtils.isEmpty(customer.getLoans())){
            logger.info(String.format("Loans for customer with id %s not found", id));
            return new ResponseEntity<>(new Message(MessageType.INFO, "Loans not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(customer.getLoans(), HttpStatus.OK);
    }

    private boolean isUserDataCorrect(Customer customer, LoanApplication application) {
        return customer.getFirstName().equals(application.getFirstName()) && customer.getLastName().equals(application.getLastName());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        logger.error(ex.getMessage());
        return new ResponseEntity<>(new Message(MessageType.ERROR, AppDefaults.BAD_REQUEST_DEFAULT_MESSAGE), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<?> handleNumberFormatException(NumberFormatException ex) {
        logger.error(ex.getMessage());
        return new ResponseEntity<>(new Message(MessageType.ERROR, AppDefaults.BAD_REQUEST_DEFAULT_MESSAGE), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        logger.error(ex.getMessage());
        return new ResponseEntity<>(new Message(MessageType.ERROR, AppDefaults.BAD_REQUEST_DEFAULT_MESSAGE), HttpStatus.BAD_REQUEST);
    }

}
