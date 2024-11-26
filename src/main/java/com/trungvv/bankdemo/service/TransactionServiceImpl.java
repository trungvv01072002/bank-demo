package com.trungvv.bankdemo.service;

import com.trungvv.bankdemo.exception.ResourceNotFoundException;
import com.trungvv.bankdemo.model.Account;
import com.trungvv.bankdemo.model.AccountStatus;
import com.trungvv.bankdemo.model.Transaction;
import com.trungvv.bankdemo.model.TransactionStatus;
import com.trungvv.bankdemo.repository.AccountRepository;
import com.trungvv.bankdemo.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    /**
     * Tạo giao dịch
     */
    @Transactional
    public Transaction createTransaction(UUID senderId, UUID receiverId, BigDecimal amount) {
        validateTransactionAmount(amount);
        Account sender = getActiveAccount(senderId, "Tài khoản gửi không hợp lệ hoặc không khả dụng");
        Account receiver = getActiveAccount(receiverId, "Tài khoản nhận không hợp lệ hoặc không khả dụng");
        validateSenderBalance(sender, amount);

        executeTransaction(sender, receiver, amount);

        return saveTransaction(senderId, receiverId, amount, TransactionStatus.SUCCESS);
    }

    /**
     * Lấy giao dịch theo ID
     */
    public Transaction getTransactionById(UUID transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giao dịch với ID: " + transactionId));
    }

    @Override
    public Transaction updateTransactionStatus(UUID transactionId, String status) {
        return null;
    }

    @Override
    public void deleteTransaction(UUID transactionId) {

    }

    /**
     * Lấy danh sách giao dịch theo User ID
     */
    public List<Transaction> listTransactionsByUserId(UUID userId) {
        return transactionRepository.findBySenderAccountIdOrReceiverAccountId(userId, userId);
    }

    @Override
    public List<Transaction> listTransactionsByAccountId(UUID accountId) {
        return List.of();
    }

    /**
     * Lấy danh sách giao dịch theo trạng thái
     */
    public List<Transaction> listTransactionsByStatus(String status) {
        TransactionStatus transactionStatus = parseTransactionStatus(status);
        return transactionRepository.findByStatus(transactionStatus);
    }

    @Override
    public List<Transaction> listTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return List.of();
    }

    /**
     * Lấy danh sách giao dịch theo khoảng thời gian
     */
    public List<Transaction> listTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByCreatedAtBetween(startDate, endDate);
    }

    /**
     * Lấy tổng số tiền giao dịch theo tài khoản
     */
    public BigDecimal getTotalTransactionsAmountByAccountId(UUID accountId) {
        return transactionRepository.sumTransactionAmountByAccountId(accountId)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Lấy số lượng giao dịch theo tài khoản
     */
    public Long getTransactionCountByAccountId(UUID accountId) {
        Account account = getActiveAccount(accountId, "Tài khoản không hợp lệ hoặc không khả dụng");
        return transactionRepository.countBySenderAccountIdOrReceiverAccountId(account);
    }

    @Override
    public Map<LocalDate, BigDecimal> getDailyTransactionSummary(LocalDate date) {
        return Map.of();
    }

    // --- Private Helper Methods ---

    /**
     * Kiểm tra số tiền giao dịch hợp lệ
     */
    private void validateTransactionAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền giao dịch phải lớn hơn 0");
        }
    }

    /**
     * Lấy tài khoản đang ở trạng thái ACTIVE
     */
    private Account getActiveAccount(UUID accountId, String errorMessage) {
        return accountRepository.findById(accountId)
                .filter(account -> account.getStatus().equals(AccountStatus.ACTIVE))
                .orElseThrow(() -> new ResourceNotFoundException(errorMessage));
    }

    /**
     * Kiểm tra số dư tài khoản gửi
     */
    private void validateSenderBalance(Account sender, BigDecimal amount) {
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Số dư tài khoản gửi không đủ để thực hiện giao dịch");
        }
    }

    /**
     * Thực hiện giao dịch: cập nhật số dư
     */
    @Transactional
    protected void executeTransaction(Account sender, Account receiver, BigDecimal amount) {
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        accountRepository.save(sender);
        accountRepository.save(receiver);
    }

    /**
     * Lưu giao dịch
     */
    @Transactional
    private Transaction saveTransaction(UUID senderId, UUID receiverId, BigDecimal amount, TransactionStatus status) {
        Transaction transaction = Transaction.builder()
                .senderAccountId(senderId)
                .receiverAccountId(receiverId)
                .amount(amount)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();
        return transactionRepository.save(transaction);
    }

    /**
     * Chuyển đổi trạng thái giao dịch từ String sang Enum
     */
    private TransactionStatus parseTransactionStatus(String status) {
        try {
            return TransactionStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Trạng thái giao dịch không hợp lệ: " + status);
        }
    }
}
