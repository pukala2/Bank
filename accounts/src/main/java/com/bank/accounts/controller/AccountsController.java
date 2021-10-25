package com.bank.accounts.controller;

import com.bank.accounts.config.AccountsServiceConfig;
import com.bank.accounts.model.*;
import com.bank.accounts.repository.AccountRepository;
import com.bank.accounts.service.client.CardsFeignClient;
import com.bank.accounts.service.client.LoansFeignClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccountsController {

    private final static Logger logger = LoggerFactory.getLogger(AccountsController.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountsServiceConfig accountsServiceConfig;

    @Autowired
    private LoansFeignClient loansFeignClient;

    @Autowired
    private CardsFeignClient cardsFeignClient;

    @PostMapping("/myAccount")
    public Accounts getAccountDetails(@RequestBody Customer customer) {

        logger.debug("myAccount method started");
        Accounts accounts = accountRepository.findByCustomerId(customer.getCustomerId());
        logger.debug("myAccount method ended");
        return accounts;
    }

    @GetMapping("/account/properties")
    public String getPropertyDetails() throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Properties properties = new Properties(accountsServiceConfig.getMsg(), accountsServiceConfig.getBuildVersion(),
                accountsServiceConfig.getMailDetails(), accountsServiceConfig.getActiveBranches());
        return ow.writeValueAsString(properties);
    }

    @PostMapping("/myCustomerDetails")
    @CircuitBreaker(name = "detailsForCustomerSupportApp",fallbackMethod ="myCustomerDetailsFallBack")
    @Retry(name = "retryForCustomerDetails", fallbackMethod = "myCustomerDetailsFallBack")
    public CustomerDetails myCustomerDetails(@RequestHeader("bank-correlation-id") String correlationId,
                                             @RequestBody Customer customer) {

        logger.debug("myCustomerDetails method started");
        Accounts accounts = accountRepository.findByCustomerId(customer.getCustomerId());
        List<Loans> loans = loansFeignClient.getLoansDetails(correlationId, customer);
        List<Cards> cards = cardsFeignClient.getCardDetails(correlationId, customer);

        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setAccounts(accounts);
        customerDetails.setLoans(loans);
        customerDetails.setCards(cards);
        logger.debug("myCustomerDetails method ended");

        return customerDetails;

    }

    private CustomerDetails myCustomerDetailsFallBack(@RequestHeader("bank-correlation-id") String correlationId,
                                                      Customer customer, Throwable t) {
        Accounts accounts = accountRepository.findByCustomerId(customer.getCustomerId());
        List<Loans> loans = loansFeignClient.getLoansDetails(correlationId, customer);
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setAccounts(accounts);
        customerDetails.setLoans(loans);
        return customerDetails;

    }

    @GetMapping("/sayHello")
    @RateLimiter(name = "sayHello", fallbackMethod = "sayHelloFallback")
    public String sayHello() {
        return "Hello, Welcome in Bank";
    }

    private String sayHelloFallback(Throwable t) {
        return "Hi, Welcome in Bank";
    }
}
