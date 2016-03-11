package com.project1.repositories;

import java.util.List;

import com.project1.model.Customer;
import org.springframework.data.repository.CrudRepository;


public interface CustomerRepository extends CrudRepository<Customer, Long> {
    Customer findById(String id);
}
