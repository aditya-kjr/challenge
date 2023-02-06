package com.dws.challenge.web;

import com.dws.challenge.domain.AmountTransferRequest;
import com.dws.challenge.domain.AmountTransferResponse;
import com.dws.challenge.exception.InvalidAccountDetailsException;
import com.dws.challenge.service.AccountTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.math.BigDecimal;

@Controller
public class AccountTransactionController {

    @Autowired
    AccountTransactionService accountTransactionService;

    @PostMapping("v1/accounts/transaction")
    public ResponseEntity<Object> transferAmount(@RequestBody @Valid AmountTransferRequest request) {

        try {
            if (request.getAmount().compareTo(BigDecimal.ZERO) == -1) {
                return new ResponseEntity<>("Amount cannot be negative", HttpStatus.BAD_REQUEST);
            }
            AmountTransferResponse response = accountTransactionService.transferMoney(request.getAmount(), request.getFromAccountId(), request.getToAccountId());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (InvalidAccountDetailsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }

    }

}
