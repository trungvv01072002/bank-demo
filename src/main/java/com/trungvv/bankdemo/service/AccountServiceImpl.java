package com.trungvv.bankdemo.service;

import com.trungvv.bankdemo.dto.AccountDto;
import com.trungvv.bankdemo.mapper.AccountMapper;
import com.trungvv.bankdemo.model.Account;
import com.trungvv.bankdemo.model.AccountStatus;
import com.trungvv.bankdemo.repository.AccountRepository;
import com.trungvv.bankdemo.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    private String generateUniqueAccountNumber() {
        String accountNumber;
        boolean isUnique = false;

        do {
            accountNumber = String.format("%08d", new Random().nextInt(100000000)); // 8 chữ số
            isUnique = accountRepository.findByAccountNumber(accountNumber).isEmpty();
        } while (!isUnique);

        return accountNumber;
    }

    @Override
    @Transactional
    public AccountDto createAccount(String accountName, BigDecimal initialBalance) {
        String accountNumber = generateUniqueAccountNumber();

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .accountName(accountName)
                .balance(initialBalance)
                .status(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
        return accountMapper.accountToAccountDto(accountRepository.save(account));
    }

    @Override
    public AccountDto getAccountById(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
        return  accountMapper.accountToAccountDto(account);
    }

    @Override
    @Transactional
    public AccountDto updateAccountStatus(UUID accountId, String status) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
        try {
            account.setStatus(AccountStatus.valueOf(status.toUpperCase()));
            account.setUpdatedAt(LocalDateTime.now());
            return accountMapper.accountToAccountDto(accountRepository.save(account));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }
    }

    @Override
    public AccountDto updateAccountBalance(UUID accountId, BigDecimal newBalance) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
        account.setBalance(newBalance);
        account.setUpdatedAt(LocalDateTime.now());
        return accountMapper.accountToAccountDto(accountRepository.save(account));
    }

    @Override
    @Transactional
    public void deleteAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
        accountRepository.delete(account);
    }

    public List<AccountDto> listAllAccounts() {
        return accountMapper.accountsToAccountDtos(accountRepository.findAll());
    }

    public List<AccountDto> listAccountsByStatus(String status) {
        try {
            AccountStatus accountStatus = AccountStatus.valueOf(status.toUpperCase());
            return accountMapper.accountsToAccountDtos(accountRepository.findByStatus(accountStatus));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }
    }

    public BigDecimal getAccountBalance(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));
        return account.getBalance();
    }
}
