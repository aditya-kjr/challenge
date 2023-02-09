package com.dws.challenge.web;


import com.dws.challenge.domain.AmountTransferRequest;
import com.dws.challenge.domain.AmountTransferResponse;
import com.dws.challenge.exception.InsufficientAccountBalanceException;
import com.dws.challenge.exception.InvalidAccountDetailsException;
import com.dws.challenge.service.AccountTransactionService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebAppConfiguration
public class AccountTransactionControllerTest {

    @Mock
    private AccountTransactionService accountTransactionService;
    @InjectMocks
    private AccountTransactionController accountTransactionController;
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {

        MockitoAnnotations.openMocks(this);

    }
    @BeforeEach
    void prepareMockMvc() {
        this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    void createAccountTransaction() throws InvalidAccountDetailsException, InsufficientAccountBalanceException {
        AmountTransferResponse responseObject = new AmountTransferResponse();
        responseObject.setStatus("SUCCESSFUL");

        AmountTransferRequest requestObject = new AmountTransferRequest();
        requestObject.setToAccountId("A0001");
        requestObject.setToAccountId("A0002");
        requestObject.setAmount(new BigDecimal(1000));
        Mockito.when(accountTransactionService.transferMoney(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(responseObject);

        ResponseEntity<Object> response = accountTransactionController.transferAmount(requestObject);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void createAccountTransactionInvalidAccountDetailsException() throws InvalidAccountDetailsException, InsufficientAccountBalanceException {
        AmountTransferRequest requestObject = new AmountTransferRequest();
        requestObject.setToAccountId("");
        requestObject.setToAccountId("");
        requestObject.setAmount(new BigDecimal(1000));

        Mockito.when(accountTransactionService.transferMoney(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(new InvalidAccountDetailsException("Invalid Account Details"));
        ResponseEntity<Object> response = accountTransactionController.transferAmount(requestObject);
        Assertions.assertEquals(422, response.getStatusCodeValue());
    }

    @Test
    void createAccountTransactionInsufficientAccountBalanceException() throws InvalidAccountDetailsException, InsufficientAccountBalanceException {
        AmountTransferRequest requestObject = new AmountTransferRequest();
        requestObject.setToAccountId("");
        requestObject.setToAccountId("");
        requestObject.setAmount(new BigDecimal(1000));

        Mockito.when(accountTransactionService.transferMoney(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(new InsufficientAccountBalanceException("Insufficient Account Balance"));
        ResponseEntity<Object> response = accountTransactionController.transferAmount(requestObject);
        Assertions.assertEquals(422, response.getStatusCodeValue());
    }

    @Test
    void negativeTransferAmountTest() {

        AmountTransferRequest requestObject = new AmountTransferRequest();
        requestObject.setToAccountId("");
        requestObject.setToAccountId("");
        requestObject.setAmount(new BigDecimal(-100));

        ResponseEntity<Object> response = accountTransactionController.transferAmount(requestObject);
        Assertions.assertEquals(400, response.getStatusCodeValue());
    }


}
