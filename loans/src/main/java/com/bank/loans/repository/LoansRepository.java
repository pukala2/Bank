package com.bank.loans.repository;

import com.bank.loans.model.Loans;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoansRepository extends JpaRepository<Loans, Long> {
    List<Loans> findByCustomerIdOrderByStartDataDesc(int customerId);
}
