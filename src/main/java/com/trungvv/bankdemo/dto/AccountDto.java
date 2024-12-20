package com.trungvv.bankdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private UUID id;
    private String accountNumber;
    private String accountName;
    private BigDecimal balance;
    private String status;
}