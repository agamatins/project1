package com.project1.controllers.customer;

import com.project1.model.Customer;
import com.project1.model.Message;
import com.project1.repositories.CustomerRepository;
import com.project1.utils.MessageType;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class CustomerController {
    final static Logger logger = LogManager.getLogger(CustomerController.class);


    @Autowired
    CustomerRepository customerRepository;

    @RequestMapping(value = "/customer/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findCustomerById(@PathVariable("id") String id) {
        logger.info("Finding customer " + id);
        Customer customer = customerRepository.findById(id);

        if (customer == null){
            logger.info(String.format("Customer with id %s not found", id));
            return new ResponseEntity<>(new Message(MessageType.INFO, "User not found"), HttpStatus.NOT_FOUND);
        }
        logger.info(String.format("Found Customer %s %s %s", id, customer.getFirstName(), customer.getLastName()));
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    @RequestMapping(value = "/customer/blacklist/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> blacklistCustomer(@PathVariable("id") String id) {
        logger.info("Blacklisting User " + id);
        Customer customer = customerRepository.findById(id);
        //can we blacklist Mr. Nobody? don't think so...
        if (customer == null){
            logger.info(String.format("Customer with id %s not found", id));
            return new ResponseEntity<>(new Message(MessageType.INFO, "Customer not found"), HttpStatus.NOT_FOUND);
        }

        //this poor guy failed again. but we can't sentence him twice unfortunately
        if (customer.isBlacklisted()){
            logger.info(String.format("Customer with id %s already blacklisted", id));
            return new ResponseEntity<>(new Message(MessageType.INFO, "User already blacklisted"), HttpStatus.ALREADY_REPORTED);
        }
        //justice happens here!
        customer.setBlacklisted(true);
        customerRepository.save(customer);

        logger.info(String.format("Customer with id %s is now blacklisted", id));
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
