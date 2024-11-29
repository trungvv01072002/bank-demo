package com.trungvv.bankdemo.repository;

import com.trungvv.bankdemo.model.Account;
import com.trungvv.bankdemo.model.AccountStatus;
import com.trungvv.bankdemo.service.AccountService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@SpringBootTest(classes = {com.trungvv.bankdemo.BankDemoApplication.class})
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    private UUID accountId = UUID.fromString("b3197930-0f9d-4c71-be28-d8ba1145601e");

    @BeforeEach
    void init() {
        accountRepository.deleteAll();
        Account account1 = Account.builder()
                .accountNumber("12345677")
                .accountName("Trung")
                .balance(BigDecimal.valueOf(1000))
                .createdAt(java.time.LocalDateTime.now())
                .status(AccountStatus.ACTIVE)
                .build();
        Account accountSaved = accountRepository.save(account1);
        accountId = accountSaved.getId();
    }

    @Test
    public void UserRepository_FindById_ThenReturnData(){
        Optional<Account> u = accountRepository.findById(accountId);
        Assertions.assertThat(u.get()).isNotNull();
    }

    @Test
    public void UserRepository_FindByAccountNumber_ThenReturnData() {
        Optional<Account> account = accountRepository.findByAccountNumber("12345677");
        Assertions.assertThat(account).isPresent();
    }

    @Test
    public void UserRepository_FindByKeySearch_ThenReturnData() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Account> accounts = accountRepository.findByKeySearch("Trung", AccountStatus.ACTIVE, pageable);
        Assertions.assertThat(accounts.getContent()).isNotEmpty();
    }

    @Test
    public void UserRepository_DeleteById_ThenRemoveData() {
        accountRepository.deleteById(accountId);
        Optional<Account> account = accountRepository.findById(accountId);
        Assertions.assertThat(account).isNotPresent();
    }

}