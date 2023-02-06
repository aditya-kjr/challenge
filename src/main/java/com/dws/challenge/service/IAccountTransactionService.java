package com.dws.challenge.service;

import com.dws.challenge.domain.AmountTransferResponse;
import com.dws.challenge.exception.InvalidAccountDetailsException;

import java.math.BigDecimal;

public interface IAccountTransactionService {


    AmountTransferResponse transferMoney(BigDecimal amount, String fromAccount, String toAccount) throws InvalidAccountDetailsException;
}
