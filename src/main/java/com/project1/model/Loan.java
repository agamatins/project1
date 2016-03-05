package com.project1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class Loan {
    @Id
    @Column(name="LOAN_ID", unique=true)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="LOAN_AMOUNT")
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name="LOAN_CUST_ID")
    private Customer customer;

    @Column(name="LOAN_COUNTRY")
    private String applicationCountry;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @JsonManagedReference
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getApplicationCountry() {
        return applicationCountry;
    }

    public void setApplicationCountry(String applicationCountry) {
        this.applicationCountry = applicationCountry;
    }

    protected Loan() {}

    public Loan(String amount, Customer customer){
        this.amount = new BigDecimal(amount);
        this.customer = customer;
    }
}
