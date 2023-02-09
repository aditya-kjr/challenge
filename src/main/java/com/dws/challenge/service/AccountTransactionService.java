package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.AmountTransferResponse;
import com.dws.challenge.exception.InsufficientAccountBalanceException;
import com.dws.challenge.exception.InvalidAccountDetailsException;
import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.utility.AccountSyncOrderResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;

@Service
public class AccountTransactionService implements IAccountTransactionService {

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private EmailNotificationService emailNotificationService;


    @Override
    public AmountTransferResponse transferMoney(BigDecimal amount, String fromAccountId, String toAccountId) throws InvalidAccountDetailsException, InsufficientAccountBalanceException {

        Account fromAccount = accountsService.getAccount(fromAccountId);
        Account toAccount = accountsService.getAccount(toAccountId);

        if (ObjectUtils.isEmpty(fromAccount) || ObjectUtils.isEmpty(toAccount)) {
            // if account not found then throw exception
            throw new InvalidAccountDetailsException("AccountId does not exists");

        } else {
            try {
                return createTransaction(amount, fromAccount, toAccount);
            } catch (InsufficientAccountBalanceException e) {
                emailNotificationService.notifyAboutTransfer(fromAccount, "Money transfer request failed due to insufficient account balance.");
                throw e;
            }

        }
    }

    private synchronized AmountTransferResponse createTransaction(BigDecimal amount, Account fromAccount, Account toAccount) throws InsufficientAccountBalanceException {

        AmountTransferResponse response = new AmountTransferResponse();
        final AccountSyncOrderResolver resolver = AccountSyncOrderResolver.resolve(toAccount, fromAccount);
        synchronized (resolver.getFirst()) {
            synchronized (resolver.getSecond()) {
                if (fromAccount.getBalance().compareTo(amount) < 0) {
                    throw new InsufficientAccountBalanceException("Insufficient Account Balance");

                } else {
                    fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
                    toAccount.setBalance(toAccount.getBalance().add(amount));
                    accountsRepository.updateAccount(fromAccount);
                    accountsRepository.updateAccount(toAccount);
                }

                //call notification service :  Amount Transfer successful
                emailNotificationService.notifyAboutTransfer(fromAccount, String.format("Money transfer request for amount: %s to AccountId : %s  was successfully processed.", amount.toString(), toAccount.getAccountId()));
                emailNotificationService.notifyAboutTransfer(toAccount, String.format("Your account is credited with amount : %s by AccountId :  %s", amount, fromAccount.getAccountId()));

                response.setStatus("SUCCESSFUL");
                response.setResponseMessage("Amount was successfully transferred");

            }
            return response;
        }

    }
}