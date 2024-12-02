package com.trungvv.bankdemo.repository;

import com.trungvv.bankdemo.dto.IDailLyTransactionSummary;
import com.trungvv.bankdemo.model.Transaction;
import com.trungvv.bankdemo.model.TransactionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    private UUID senderId;
    private UUID receiverId;
    private Transaction transaction1;
    private Transaction transaction2;

    @BeforeEach
    void setUp() {
        senderId = UUID.randomUUID();
        receiverId = UUID.randomUUID();

        transaction1 = Transaction.builder()
                .senderAccountId(senderId)
                .receiverAccountId(receiverId)
                .amount(BigDecimal.valueOf(1000))
                .status(TransactionStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        transaction2 = Transaction.builder()
                .senderAccountId(receiverId)
                .receiverAccountId(senderId)
                .amount(BigDecimal.valueOf(500))
                .status(TransactionStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
    }

    @Test
    void findBySenderAccountIdOrReceiverAccountId_ShouldReturnTransactions() {
        List<Transaction> transactions = transactionRepository.findBySenderAccountIdOrReceiverAccountId(senderId, senderId);

        assertNotNull(transactions);
        assertEquals(2, transactions.size());
    }

    @Test
    void findByCreatedAtBetween_ShouldReturnTransactionsInDateRange() {
        LocalDate startDate = LocalDate.now().minusDays(2);
        LocalDate endDate = LocalDate.now();

        List<Transaction> transactions = transactionRepository.findByCreatedAtBetween(
                startDate, endDate, senderId, null,"ALL", Pageable.unpaged()).getContent();

        assertNotNull(transactions);
        assertEquals(2, transactions.size());
    }

    @Test
    void sumTransactionAmountByAccountId_ShouldReturnTotalAmount() {
        Optional<BigDecimal> totalAmount = transactionRepository.sumTransactionAmountByAccountId(senderId);

        assertTrue(totalAmount.isPresent());
        assertEquals(BigDecimal.valueOf(1500.00).setScale(2), totalAmount.get().setScale(2));
    }



    @Test
    void getDailyTransactionSummaryForAllCustomers_ShouldReturnSummary() {
        LocalDate today = LocalDate.now();
        List<IDailLyTransactionSummary> summaries = transactionRepository.getDailyTransactionSummaryForAllCustomers(today);

        assertNotNull(summaries);
        assertFalse(summaries.isEmpty());

        boolean found = false;
        for (IDailLyTransactionSummary summary : summaries) {
            if (senderId.equals(UUID.fromString(summary.getAccountId()))) {
                assertEquals(today, summary.getDate());
                assertEquals(senderId, UUID.fromString(summary.getAccountId()));
                found = true;
                break;
            }
        }
        assertTrue(found, "Summary for senderId not found");
    }
}
