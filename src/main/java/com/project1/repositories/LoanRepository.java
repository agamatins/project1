package com.project1.repositories;

import com.project1.model.Loan;
import org.springframework.data.repository.CrudRepository;

public interface LoanRepository extends CrudRepository<Loan, Long> {
//    @Query("SELECT l FROM Loan lt WHERE lt.LOAN_CUST_ID = :id)")
//    List<Loan> findByUserId(@Param("id")String id);
}
