package com.trungvv.bankdemo.service;

import com.trungvv.bankdemo.dto.AccountDto;
import com.trungvv.bankdemo.dto.IDailLyTransactionSummary;
import com.trungvv.bankdemo.dto.TransactionDto;
import com.trungvv.bankdemo.exception.ResourceNotFoundException;
import com.trungvv.bankdemo.mapper.TransactionMapper;
import com.trungvv.bankdemo.model.Account;
import com.trungvv.bankdemo.model.AccountStatus;
import com.trungvv.bankdemo.model.Transaction;
import com.trungvv.bankdemo.model.TransactionStatus;
import com.trungvv.bankdemo.repository.AccountRepository;
import com.trungvv.bankdemo.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    /**
     * Tạo giao dịch
     */
    @Transactional
    public TransactionDto createTransaction(UUID senderId, UUID receiverId, BigDecimal amount, String message) {
        validateSenderAndReceiver(senderId, receiverId);
        validateTransactionAmount(amount);
        Account sender = getActiveAccount(senderId, "Tài khoản gửi không hợp lệ hoặc không khả dụng");
        Account receiver = getActiveAccount(receiverId, "Tài khoản nhận không hợp lệ hoặc không khả dụng");
        validateSenderBalance(sender, amount);
        executeTransaction(sender, receiver, amount);
        return saveTransaction(senderId, receiverId, amount, TransactionStatus.SUCCESS, message);
    }

    /**
     * Lấy giao dịch theo ID
     */
    public Transaction getTransactionById(UUID transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giao dịch với ID: " + transactionId));
    }

    @Override
    public TransactionDto updateTransactionStatus(UUID transactionId, String status) {
        Transaction transaction = getTransactionById(transactionId);
        TransactionStatus newStatus = parseTransactionStatus(status);
        transaction.setStatus(newStatus);
        return transactionMapper.transactionToTransactionDto(transactionRepository.save(transaction));
    }

    @Override
    public void deleteTransaction(UUID transactionId) {
        transactionRepository.deleteById(transactionId);
    }

    /**
     * Lấy danh sách giao dịch theo User ID
     */
    public List<TransactionDto> listTransactionsByUserId(UUID userId) {
        return transactionMapper.transactionsToTransactionDtos(transactionRepository.findBySenderAccountIdOrReceiverAccountId(userId, userId));
    }


    /**
     * Lấy danh sách giao dịch theo khoảng thời gian
     */
    @Override
    public Page<TransactionDto> listTransactionsByDateRange(LocalDate startDate, LocalDate endDate, UUID accountId, String status, int page, int size) {

        TransactionStatus transactionStatus = (status == null || status.trim().isEmpty()) ? null : TransactionStatus.valueOf(status.toUpperCase());
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactionPage = transactionRepository.findByCreatedAtBetween(startDate, endDate, accountId, transactionStatus, pageable);
        List<TransactionDto> transactionDtos = transactionMapper.transactionsToTransactionDtos(transactionPage.getContent());
        return new PageImpl<>(transactionDtos, pageable, transactionPage.getTotalElements());

    }

    /**
     * Lấy tổng số tiền giao dịch theo tài khoản
     */
    public BigDecimal getTotalTransactionsAmountByAccountId(UUID accountId) {
        return transactionRepository.sumTransactionAmountByAccountId(accountId)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public List<IDailLyTransactionSummary> getDailyTransactionSummary(LocalDate date) {
        return transactionRepository.getDailyTransactionSummaryForAllCustomers(date);
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

    private void validateSenderAndReceiver(UUID senderId, UUID receiverId) {
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Sender và receiver không thể giống nhau");
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
    TransactionDto saveTransaction(UUID senderId, UUID receiverId, BigDecimal amount, TransactionStatus status, String message) {
        Transaction transaction = Transaction.builder()
                .senderAccountId(senderId)
                .receiverAccountId(receiverId)
                .amount(amount)
                .status(status)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        return transactionMapper.transactionToTransactionDto(transactionRepository.save(transaction));
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
