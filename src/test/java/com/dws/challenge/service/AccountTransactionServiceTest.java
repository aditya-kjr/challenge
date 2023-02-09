package com.dws.challenge.service;


import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.AmountTransferResponse;
import com.dws.challenge.exception.InsufficientAccountBalanceException;
import com.dws.challenge.exception.InvalidAccountDetailsException;
import com.dws.challenge.repository.AccountsRepository;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebAppConfiguration
public class AccountTransactionServiceTest {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Mock
    private AccountsService accountsService;
    @Mock
    private EmailNotificationService emailNotificationService;
    @Mock
    private AccountsRepository accountsRepository;
    @InjectMocks
    private AccountTransactionService accountTransactionService;


    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void prepareMockMvc() {
        this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void givenInvalidAccountDetailsThenThrowException() {

        Mockito.when(accountsService.getAccount(Mockito.any())).thenReturn(null).thenReturn(null);
        Assertions.assertThrowsExactly(InvalidAccountDetailsException.class, () -> accountTransactionService.transferMoney(new BigDecimal(100), "A001", "A002"));

    }

    @Test
    void givenInsufficientBalanceThenThrowException() {
        Account fromAccount = new Account("A001", new BigDecimal(0));
        Account toAccount = new Account("A002", new BigDecimal(300));

        Mockito.when(accountsService.getAccount(Mockito.any())).thenReturn(fromAccount).thenReturn(toAccount);
        Assertions.assertThrowsExactly(InsufficientAccountBalanceException.class, () -> accountTransactionService.transferMoney(new BigDecimal(500), "A001", "A002"));

    }

    @Test
    void testCreateTransactionSuccessful() throws InvalidAccountDetailsException, InsufficientAccountBalanceException {

        Account fromAccount = new Account("A001", new BigDecimal(1000));
        Account toAccount = new Account("A002", new BigDecimal(0));
        Mockito.when(accountsService.getAccount(Mockito.any())).thenReturn(fromAccount).thenReturn(toAccount);

        AmountTransferResponse response = accountTransactionService.transferMoney(new BigDecimal(500), "A001", "A002");
        Assertions.assertEquals("SUCCESSFUL", response.getStatus());
        Assertions.assertEquals("Amount was successfully transferred", response.getResponseMessage());

    }


}
