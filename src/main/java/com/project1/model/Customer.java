package com.project1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.List;

@Entity
public class Customer {

    @Id
    @Column(name="CUST_ID", unique=true)
    @NotBlank (message="id cannot be empty")
    private String id;

    @NotBlank (message="firstName cannot be empty")
    @Column(name="CUST_FIRST_NAME")
    private String firstName;

    @NotBlank (message="lastName cannot be empty")
    @Column(name="CUST_LAST_NAME")
    private String lastName;

    @Column(name="CUST_BLACKLIST")
    private boolean blacklisted = false;

    @OneToMany(fetch = FetchType.EAGER, mappedBy="customer", cascade = CascadeType.ALL)
    private List<Loan> loans;

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isBlacklisted(){
        return blacklisted;
    }

    public void setBlacklisted(boolean blacklisted){
        this.blacklisted = blacklisted;
    }

    @JsonBackReference
    public List<Loan> getLoans() {
        return loans;
    }

    public void setLoans(List<Loan> loans) {
        this.loans = loans;
    }

    protected Customer() {}

    public Customer(String id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%d, firstName='%s', lastName='%s']",
                id, firstName, lastName);
    }

}

