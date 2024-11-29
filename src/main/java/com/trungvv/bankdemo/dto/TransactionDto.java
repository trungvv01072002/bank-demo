package com.trungvv.bankdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {
    private UUID id;
    private UUID senderAccountId;
    private UUID receiverAccountId;
    private BigDecimal amount;
    private String status;
    private String description;
}
