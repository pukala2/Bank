package com.bank.accounts.repository;

import com.bank.accounts.model.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface AccountRepository extends JpaRepository<Accounts, Long> {
    Accounts findByCustomerId(int customerId);
}
