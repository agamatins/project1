package com.project1.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.*;
import java.math.BigDecimal;

public class LoanApplication {
    @NotBlank(message="customerId cannot be empty")
    private String customerId;
    @NotBlank(message="firstName cannot be empty")
    @Pattern(regexp = "^[A-Za-z]*$", message="firstName should contain only letters")
    private String firstName;
    @NotBlank(message="lastName cannot be empty")
    @Pattern(regexp = "^[A-Za-z]*$", message="lastName should contain only letters")
    private String lastName;
    @NotNull(message="amount cannot be empty")
    @Digits(fraction = 2,integer = 4, message="Incorrect amount format")
    @Min(value=0, message="Loan amount should not be less than minimum")
    @Max(value=1000, message="Loan amount should not be more than maximum")
    private BigDecimal amount;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
