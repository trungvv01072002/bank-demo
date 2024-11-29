package com.trungvv.bankdemo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface IDailLyTransactionSummary {
    String getAccountId();
    Long getTransactionCount();
    LocalDate getDate();
    BigDecimal getTotalAmount();
}
