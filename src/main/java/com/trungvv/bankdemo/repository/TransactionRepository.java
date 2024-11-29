package com.trungvv.bankdemo.repository;

import com.trungvv.bankdemo.dto.IDailLyTransactionSummary;
import com.trungvv.bankdemo.model.Account;
import com.trungvv.bankdemo.model.Transaction;
import com.trungvv.bankdemo.model.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    /**
     * Tìm giao dịch theo Sender hoặc Receiver Account ID.
     */
    List<Transaction> findBySenderAccountIdOrReceiverAccountId(UUID senderAccountId, UUID receiverAccountId);

//    /**
//     * Tìm giao dịch theo trạng thái.
//     */
//    List<Transaction> findByStatus(TransactionStatus status);

    /**
     * Tìm giao dịch trong khoảng thời gian.
     */
    @Query("SELECT t " +
            "FROM Transaction t " +
            "WHERE CAST(t.createdAt AS date) BETWEEN :startDate AND :endDate " +
            "AND (t.senderAccountId = :accountId OR t.receiverAccountId = :accountId)")
    Page<Transaction> findByCreatedAtBetween(LocalDate startDate, LocalDate endDate, UUID accountId, TransactionStatus status , Pageable pageable);

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
    @Query("SELECT CAST(t.createdAt AS date) AS date, t.senderAccountId AS accountId, COUNT(t) AS transactionCount, COALESCE(SUM(t.amount), 0) AS totalAmount " +
            "FROM Transaction t " +
            "WHERE CAST(t.createdAt AS date) = :date " +
            "GROUP BY CAST(t.createdAt AS date), t.senderAccountId")
    List<IDailLyTransactionSummary> getDailyTransactionSummaryForAllCustomers(@Param("date") LocalDate date);

}
