package com.bank.loans.controller;

import com.bank.loans.config.LoansServiceConfig;
import com.bank.loans.model.Customer;
import com.bank.loans.model.Loans;
import com.bank.loans.model.Properties;
import com.bank.loans.repository.LoansRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LoansController {

    private final static Logger logger = LoggerFactory.getLogger(LoansController.class);

    @Autowired
    private LoansRepository loansRepository;

    @Autowired
    LoansServiceConfig loansServiceConfig;

    @PostMapping("/myLoans")
    public List<Loans> getLoansDetails(@RequestHeader("bank-correlation-id") String correlationId,
                                       @RequestBody Customer customer) {
        logger.debug("myLoans method started");
        List<Loans> loans = loansRepository.findByCustomerIdOrderByStartDataDesc(customer.getCustomerId());
        logger.debug("myLoans method ended");
        return loans;
    }

    @GetMapping("/loans/properties")
    String getPropertyDetails() throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Properties properties = new Properties(loansServiceConfig.getMsg(), loansServiceConfig.getBuildVersion(),
                loansServiceConfig.getMailDetails(), loansServiceConfig.getActiveBranches());
        return ow.writeValueAsString(properties);
    }


}
