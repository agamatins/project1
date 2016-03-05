package com.project1.controllers.customer;

import com.project1.model.Customer;
import com.project1.model.Message;
import com.project1.repositories.CustomerRepository;
import com.project1.utils.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class CustomerController {

    @Autowired
    CustomerRepository customerRepository;

    @RequestMapping(value = "/customer/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findCustomerById(@PathVariable("id") String id) {
        System.out.println("Finding User " + id);
        Customer customer = customerRepository.findById(id);

        if (customer == null){
            System.out.println("Nothing found");
            return new ResponseEntity<Message>(new Message(MessageType.INFO, "User not found"), HttpStatus.NOT_FOUND);
        }
        System.out.println("Found User " + customer.getId() + " " + customer.getFirstName() + " " + customer.getLastName());
        return new ResponseEntity<Customer>(customer, HttpStatus.OK);
    }

    @RequestMapping(value = "/customer/blacklist/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> blacklistCustomer(@PathVariable("id") String id) {
        System.out.println("Blacklisting User " + id);
        Customer customer = customerRepository.findById(id);
        //can we blacklist Mr. Nobody? don't think so...
        if (customer == null){
            System.out.println("User not found");
            return new ResponseEntity<Message>(new Message(MessageType.INFO, "User not found"), HttpStatus.NOT_FOUND);
        }

        //this poor guy failed again. but we can't sentence him twice unfortunately
        if (customer.isBlacklisted()){
            System.out.println("User already blacklisted");
            return new ResponseEntity<Message>(new Message(MessageType.INFO, "User already blacklisted"), HttpStatus.ALREADY_REPORTED);
        }
        //justice happens here!
        customer.setBlacklisted(true);
        customerRepository.save(customer);

        System.out.println("Customer " + customer.getId() + " is now blacklisted");
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
