package com.trungvv.bankdemo.controller;

import com.trungvv.bankdemo.model.Transaction;
import com.trungvv.bankdemo.service.TransactionService;
import com.trungvv.bankdemo.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
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
                                                         @RequestParam BigDecimal amount) {
        try {
            Transaction transaction = transactionService.createTransaction(UUID.fromString(senderId), UUID.fromString(receiverId), amount);
            return new ResponseEntity<>(transaction, HttpStatus.CREATED);
        } catch (Exception e) {
            // Catch any exceptions that occur during the transaction creation process
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get a transaction by its ID
    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable String transactionId) {
        try {
            Transaction transaction = transactionService.getTransactionById(UUID.fromString(transactionId));
            if (transaction != null) {
                return ResponseEntity.ok(transaction);
            } else {
                throw new ResourceNotFoundException("Transaction not found with ID " + transactionId);
            }
        } catch (ResourceNotFoundException e) {
            // Return 404 if the resource is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            // Catch any other exceptions
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update transaction status
    @PutMapping("/{transactionId}/status")
    public ResponseEntity<Transaction> updateTransactionStatus(@PathVariable String transactionId,
                                                               @RequestParam String status) {
        try {
            Transaction transaction = transactionService.updateTransactionStatus(UUID.fromString(transactionId), status);
            if (transaction != null) {
                return ResponseEntity.ok(transaction);
            } else {
                throw new ResourceNotFoundException("Transaction not found with ID " + transactionId);
            }
        } catch (ResourceNotFoundException e) {
            // Return 404 if the transaction is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            // Catch any other exceptions
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete a transaction
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable String transactionId) {
        try {
            transactionService.deleteTransaction(UUID.fromString(transactionId));
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // Catch any exceptions during delete
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // List transactions by user ID (Sender or Receiver)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Transaction>> listTransactionsByUserId(@PathVariable String userId) {
        try {
            List<Transaction> transactions = transactionService.listTransactionsByUserId(UUID.fromString(userId));
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // List transactions by account ID (Sender or Receiver)
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Transaction>> listTransactionsByAccountId(@PathVariable String accountId) {
        try {
            List<Transaction> transactions = transactionService.listTransactionsByAccountId(UUID.fromString(accountId));
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // List transactions by status
    @GetMapping("/status")
    public ResponseEntity<List<Transaction>> listTransactionsByStatus(@RequestParam String status) {
        try {
            List<Transaction> transactions = transactionService.listTransactionsByStatus(status);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // List transactions within a date range
    @GetMapping("/date-range")
    public ResponseEntity<List<Transaction>> listTransactionsByDateRange(@RequestParam LocalDate startDate,
                                                                         @RequestParam LocalDate endDate) {
        try {
            List<Transaction> transactions = transactionService.listTransactionsByDateRange(startDate, endDate);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get total transaction amount for an account
    @GetMapping("/total-amount/{accountId}")
    public ResponseEntity<BigDecimal> getTotalTransactionsAmountByAccountId(@PathVariable String accountId) {
        try {
            BigDecimal totalAmount = transactionService.getTotalTransactionsAmountByAccountId(UUID.fromString(accountId));
            return ResponseEntity.ok(totalAmount);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get the transaction count for an account
    @GetMapping("/count/{accountId}")
    public ResponseEntity<Long> getTransactionCountByAccountId(@PathVariable String accountId) {
        try {
            Long transactionCount = transactionService.getTransactionCountByAccountId(UUID.fromString(accountId));
            return ResponseEntity.ok(transactionCount);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get daily transaction summary
    @GetMapping("/daily-summary")
    public ResponseEntity<Map<LocalDate, BigDecimal>> getDailyTransactionSummary(@RequestParam LocalDate date) {
        try {
            Map<LocalDate, BigDecimal> dailySummary = transactionService.getDailyTransactionSummary(date);
            return ResponseEntity.ok(dailySummary);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
