package com.project1.repositories;

import com.project1.model.Loan;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoanRepository extends CrudRepository<Loan, Long> {
//    @Query("SELECT l FROM Loan lt WHERE lt.LOAN_CUST_ID = :id)")
//    List<Loan> findByUserId(@Param("id")String id);
}
