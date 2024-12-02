package com.trungvv.bankdemo.controller;

import com.trungvv.bankdemo.dto.TransactionDto;
import com.trungvv.bankdemo.model.Transaction;
import com.trungvv.bankdemo.service.TransactionService;
import com.trungvv.bankdemo.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // Create a new transaction
    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestParam String senderId,
                                                         @RequestParam String receiverId,
                                                         @RequestParam BigDecimal amount,
                                                        @RequestParam String message) {
        try {
            TransactionDto transaction = transactionService.createTransaction(UUID.fromString(senderId), UUID.fromString(receiverId), amount, message);
            return new ResponseEntity<>(transaction, HttpStatus.CREATED);
        } catch (Exception e) {
            // Catch any exceptions that occur during the transaction creation process
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get a transaction by its ID
    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getTransactionById(@PathVariable String transactionId) {
        try {
            Transaction transaction = transactionService.getTransactionById(UUID.fromString(transactionId));
            if (transaction != null) {
                return ResponseEntity.ok(transaction);
            } else {
                throw new ResourceNotFoundException("Transaction not found with ID " + transactionId);
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update transaction status
    @PutMapping("/{transactionId}/status")
    public ResponseEntity<?> updateTransactionStatus(@PathVariable String transactionId,
                                                               @RequestParam String status) {
        try {
            TransactionDto transaction = transactionService.updateTransactionStatus(UUID.fromString(transactionId), status);
            if (transaction != null) {
                return ResponseEntity.ok(transaction);
            } else {
                throw new ResourceNotFoundException("Transaction not found with ID " + transactionId);
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete a transaction
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<?> deleteTransaction(@PathVariable String transactionId) {
        try {
            transactionService.deleteTransaction(UUID.fromString(transactionId));
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // Catch any exceptions during delete
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // List transactions by user ID (Sender or Receiver)
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> listTransactionsByUserId(@PathVariable String userId) {
        try {
            List<TransactionDto> transactions = transactionService.listTransactionsByUserId(UUID.fromString(userId));
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // List transactions within a date range
    @GetMapping("/date-range")
    public ResponseEntity<?> listTransactionsByDateRange(@RequestParam LocalDate startDate,
                                                                         @RequestParam LocalDate endDate,
                                                                         @RequestParam String accountId,
                                                                         @RequestParam String status,
                                                                         @RequestParam int page,
                                                                        @RequestParam int size,
                                                                        @RequestParam String typeTransaction
                                                                ) {
        try {
            Page<TransactionDto> transactions = transactionService.listTransactionsByDateRange(startDate, endDate, UUID.fromString(accountId), status,typeTransaction, page, size);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get total transaction amount for an account
    @GetMapping("/total-amount/{accountId}")
    public ResponseEntity<?> getTotalTransactionsAmountByAccountId(@PathVariable String accountId) {
        try {
            BigDecimal totalAmount = transactionService.getTotalTransactionsAmountByAccountId(UUID.fromString(accountId));
            return ResponseEntity.ok(totalAmount);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get daily transaction summary
    @GetMapping("/daily-summary")
    public ResponseEntity<?> getDailyTransactionSummary(@RequestParam LocalDate date) {
        try {
            return ResponseEntity.ok(transactionService.getDailyTransactionSummary(date));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
