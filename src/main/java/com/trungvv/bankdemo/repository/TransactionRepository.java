package com.trungvv.bankdemo.repository;

import com.trungvv.bankdemo.model.Account;
import com.trungvv.bankdemo.model.Transaction;
import com.trungvv.bankdemo.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    /**
     * Tìm giao dịch theo Sender hoặc Receiver Account ID.
     */
    List<Transaction> findBySenderAccountIdOrReceiverAccountId(UUID senderAccountId, UUID receiverAccountId);

    /**
     * Tìm giao dịch theo trạng thái.
     */
    List<Transaction> findByStatus(TransactionStatus status);

    /**
     * Tìm giao dịch trong khoảng thời gian.
     */
    List<Transaction> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Tính tổng số tiền giao dịch theo Account ID.
     * Bao gồm cả giao dịch gửi và nhận.
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) " +
            "FROM Transaction t " +
            "WHERE t.senderAccountId = :accountId OR t.receiverAccountId = :accountId")
    Optional<BigDecimal> sumTransactionAmountByAccountId(@Param("accountId") UUID accountId);

    /**
     * Đếm số lượng giao dịch theo Account ID.
     * Bao gồm cả giao dịch gửi và nhận.
     */
    @Query("SELECT COUNT(t) " +
            "FROM Transaction t " +
            "WHERE t.senderAccountId = :accountId OR t.receiverAccountId = :accountId")
    Long countBySenderAccountIdOrReceiverAccountId(@Param("accountId") Account account);

    /**
     * Báo cáo giao dịch hàng ngày: Tổng số tiền giao dịch theo ngày.
     */
    @Query("SELECT t.createdAt AS date, COALESCE(SUM(t.amount), 0) AS totalAmount " +
            "FROM Transaction t " +
            "WHERE DATE(t.createdAt) = :date " +
            "GROUP BY DATE(t.createdAt)")
    List<Object[]> getDailyTransactionSummary(@Param("date") LocalDateTime date);
}
