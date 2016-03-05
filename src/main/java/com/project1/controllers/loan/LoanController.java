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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
public class LoanController {

    @Autowired
    RequestService requestService;
    @Autowired
    LoanRepository loanRepository;
    @Autowired
    CustomerRepository customerRepository;

    @RequestMapping(value = "/loan/", method = RequestMethod.POST)
    public ResponseEntity<?> createLoan(@Valid @RequestBody LoanApplication application, UriComponentsBuilder ucBuilder, HttpServletRequest request) {
        System.out.println("Creating Loan Application");

        System.out.println("Request ip " + request.getRemoteAddr());
        String country = ((request.getRemoteAddr() != null) ? requestService.getCountry(request.getRemoteAddr()) : AppDefaults.DEFAULT_COUNTRY);
        //TODO: request spamming from country check

        //check customer
        Customer customer = customerRepository.findById(application.getCustomerId());
        if (customer != null){
            //okwe got you in the db.
            //check is data in application matching data in the db. we can't allow users with the same id have different names
            if (!isUserDataCorrect(customer, application)){
                System.out.println("User data conflict");
                return new ResponseEntity<Message>(new Message(MessageType.INFO, "Wrong personal data for given user"), HttpStatus.CONFLICT);
            }

            //check if user is blacklisted. we don't deal with bad men. at lest he's been blacklisted for a reason! or not?
            if (customer.isBlacklisted()){
                System.out.println("Rejected as user is blacklisted");
                return new ResponseEntity<Message>(new Message(MessageType.INFO, "Rejected as customer is blacklisted"), HttpStatus.EXPECTATION_FAILED);
            }

        } else {
            //we don't have user with this id in our database. let's create one.
            customer = new Customer(application.getCustomerId(), application.getFirstName(), application.getLastName());
        }
        customerRepository.save(customer);
        System.out.println("Customer created " + customer.getId());

        //now we can create loan application
        Loan loan = new Loan(application.getAmount(), customer);
        loan.setApplicationCountry(country);
        loanRepository.save(loan);
        System.out.println("Loan application created " + loan.getId());
        //could be nice to include url for a newly created loan to header
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/loan/{id}").buildAndExpand(loan.getId()).toUri());
        return new ResponseEntity<Void>(null, headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/loan/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findLoanById(@PathVariable("id") long id) {
        System.out.println("Finding User " + id);
        Loan loan = loanRepository.findOne(id);

        if (loan == null){
            System.out.println("Nothing found");
            return new ResponseEntity<Message>(new Message(MessageType.INFO, "Loan not found"), HttpStatus.NOT_FOUND);
        }
        System.out.println("Found Loan Application " + loan.getId() + " " + loan.getCustomer().getId() + " " + loan.getAmount().toString());
        return new ResponseEntity<Loan>(loan, HttpStatus.OK);
    }

    @RequestMapping(value = "/loans/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAllLoans() {
        System.out.println("Finding All Approved Loan Applications");
        Iterable<Loan> loans = loanRepository.findAll();

        if (loans == null){
            System.out.println("Nothing found");
            return new ResponseEntity<Message>(new Message(MessageType.INFO, "Loans not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Iterable<Loan>>(loans, HttpStatus.OK);
    }

    @RequestMapping(value = "/loans/list_for_user/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAllLoansForUser(@PathVariable("id") String id) {
        System.out.println("Finding All Approved Loan Applications For User " + id);
        Customer customer = customerRepository.findById(id);

        //do we have user with this id?
        if (customer == null){
            System.out.println("Customer not found");
            return new ResponseEntity<Message>(new Message(MessageType.INFO, "Customer not found"), HttpStatus.NOT_FOUND);
        }

        //does this user have any loan applications?
        if (CollectionUtils.isEmpty(customer.getLoans())){
            System.out.println("Loans not found for customer");
            return new ResponseEntity<Message>(new Message(MessageType.INFO, "Loans not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<Loan>>(customer.getLoans(), HttpStatus.OK);
    }

    private boolean isUserDataCorrect(Customer customer, LoanApplication application) {
        return customer.getFirstName().equals(application.getFirstName()) && customer.getLastName().equals(application.getLastName());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleIOException(HttpMessageNotReadableException ex) {
        return new ResponseEntity<Message>(new Message(MessageType.ERROR, "Invalid request body"), HttpStatus.BAD_REQUEST);
    }
}
