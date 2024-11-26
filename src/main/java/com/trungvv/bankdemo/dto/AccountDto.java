package com.trungvv.bankdemo.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AccountDto {
    private UUID id;
    private String accountNumber;
    private String accountName;
    private BigDecimal balance;
    private String status;
}