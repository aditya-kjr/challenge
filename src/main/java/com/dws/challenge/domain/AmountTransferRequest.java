package com.dws.challenge.domain;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class AmountTransferRequest {
    @NotNull
    @NotEmpty
    private String fromAccountId;
    @NotNull
    @NotEmpty
    private String toAccountId;
    @NotNull
    private BigDecimal amount;
}
