package com.trungvv.bankdemo.service;

import com.trungvv.bankdemo.dto.TransactionDto;
import com.trungvv.bankdemo.exception.ResourceNotFoundException;
import com.trungvv.bankdemo.mapper.TransactionMapper;
import com.trungvv.bankdemo.model.Account;
import com.trungvv.bankdemo.model.AccountStatus;
import com.trungvv.bankdemo.model.Transaction;
import com.trungvv.bankdemo.model.TransactionStatus;
import com.trungvv.bankdemo.repository.AccountRepository;
import com.trungvv.bankdemo.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    private UUID senderId;
    private UUID receiverId;
    private Transaction transaction;
    private TransactionDto transactionDto;
    private Account sender;
    private Account receiver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        senderId = UUID.randomUUID();
        receiverId = UUID.randomUUID();

        sender = Account.builder()
                .id(senderId)
                .accountNumber("111111")
                .balance(BigDecimal.valueOf(1000))
                .status(AccountStatus.ACTIVE)
                .build();

        receiver = Account.builder()
                .id(receiverId)
                .accountNumber("222222")
                .balance(BigDecimal.valueOf(500))
                .status(AccountStatus.ACTIVE)
                .build();

        transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .senderAccountId(senderId)
                .receiverAccountId(receiverId)
                .amount(BigDecimal.valueOf(200))
                .status(TransactionStatus.SUCCESS)
                .message("Test transaction")
                .createdAt(LocalDateTime.now())
                .build();

        transactionDto = TransactionDto.builder()
                .id(transaction.getId())
                .senderAccountId(senderId)
                .receiverAccountId(receiverId)
                .amount(BigDecimal.valueOf(200))
                .status("SUCCESS")
                .description("Test transaction")
                .build();
    }

    @Test
    void createTransaction_ShouldReturnCreatedTransaction() {
        when(accountRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(accountRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(transactionMapper.transactionToTransactionDto(transaction)).thenReturn(transactionDto);

        TransactionDto result = transactionService.createTransaction(senderId, receiverId, BigDecimal.valueOf(200), "Test transaction");

        assertNotNull(result);
        assertEquals("Test transaction", result.getDescription());
        verify(accountRepository, times(2)).save(any(Account.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void getTransactionById_ShouldReturnTransaction_WhenTransactionExists() {
        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        Transaction result = transactionService.getTransactionById(transaction.getId());

        assertNotNull(result);
        assertEquals(transaction.getId(), result.getId());
        verify(transactionRepository, times(1)).findById(transaction.getId());
    }

    @Test
    void getTransactionById_ShouldThrowException_WhenTransactionNotFound() {
        UUID transactionId = UUID.randomUUID();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.getTransactionById(transactionId));
    }

    @Test
    void listTransactionsByDateRange_ShouldReturnPagedResults() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        int page = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);

        List<Transaction> transactions = List.of(transaction);
        Page<Transaction> transactionPage = new PageImpl<>(transactions, pageable, transactions.size());
        when(transactionRepository.findByCreatedAtBetween(startDate, endDate, senderId, null, pageable)).thenReturn(transactionPage);
        when(transactionMapper.transactionsToTransactionDtos(transactions)).thenReturn(List.of(transactionDto));

        Page<TransactionDto> result = transactionService.listTransactionsByDateRange(startDate, endDate, senderId, null, page, size);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(transactionDto.getDescription(), result.getContent().get(0).getDescription());
        verify(transactionRepository, times(1)).findByCreatedAtBetween(startDate, endDate, senderId, null, pageable);
    }

    @Test
    void deleteTransaction_ShouldRemoveTransaction_WhenTransactionExists() {
        UUID transactionId = transaction.getId();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        transactionService.deleteTransaction(transactionId);

        verify(transactionRepository, times(1)).deleteById(transactionId);
    }
}
