package com.trungvv.bankdemo.service;

import com.trungvv.bankdemo.dto.IDailLyTransactionSummary;
import com.trungvv.bankdemo.dto.TransactionDto;
import com.trungvv.bankdemo.model.Transaction;
import com.trungvv.bankdemo.model.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TransactionService {
    TransactionDto createTransaction(UUID senderId, UUID receiverId, BigDecimal amount, String message);
    Transaction getTransactionById(UUID transactionId);
    TransactionDto updateTransactionStatus(UUID transactionId, String status);
    void deleteTransaction(UUID transactionId);
    List<TransactionDto> listTransactionsByUserId(UUID userId);
    Page<TransactionDto> listTransactionsByDateRange(LocalDate startDate, LocalDate endDate, UUID accountId, String status, String typeTransaction, int page, int size);
    BigDecimal getTotalTransactionsAmountByAccountId(UUID accountId);
    List<IDailLyTransactionSummary> getDailyTransactionSummary(LocalDate date);
}
