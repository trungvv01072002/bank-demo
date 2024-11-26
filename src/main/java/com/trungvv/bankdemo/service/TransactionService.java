package com.trungvv.bankdemo.service;

import com.trungvv.bankdemo.model.Transaction;
import com.trungvv.bankdemo.model.TransactionStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TransactionService {
    Transaction createTransaction(UUID senderId, UUID receiverId, BigDecimal amount);
    Transaction getTransactionById(UUID transactionId);
    Transaction updateTransactionStatus(UUID transactionId, String status);
    void deleteTransaction(UUID transactionId);
    List<Transaction> listTransactionsByUserId(UUID userId);
    List<Transaction> listTransactionsByAccountId(UUID accountId);
    List<Transaction> listTransactionsByStatus(String status);
    List<Transaction> listTransactionsByDateRange(LocalDate startDate, LocalDate endDate);
    BigDecimal getTotalTransactionsAmountByAccountId(UUID accountId);
    Long getTransactionCountByAccountId(UUID accountId);
    Map<LocalDate, BigDecimal> getDailyTransactionSummary(LocalDate date);
}
