package com.trungvv.bankdemo.repository;

import com.trungvv.bankdemo.model.Account;
import com.trungvv.bankdemo.model.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByAccountNumber(String accountNumber);
    @Query("SELECT a FROM Account a WHERE (:status IS NULL OR a.status = :status) AND (a.accountName LIKE %:keySearch% OR a.accountNumber LIKE %:keySearch%)")
    Page<Account> findByKeySearch(String keySearch, AccountStatus status, Pageable pageable);

}
