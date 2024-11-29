package com.trungvv.bankdemo.service;

import com.trungvv.bankdemo.dto.AccountDto;
import com.trungvv.bankdemo.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface AccountService {
    AccountDto createAccount(String name, BigDecimal initialBalance);
    AccountDto getAccountById(UUID accountId);
    AccountDto updateAccountStatus(UUID accountId, String status);
    AccountDto updateAccountBalance(UUID accountId, BigDecimal newBalance);
    void deleteAccount(UUID accountId);
    List<AccountDto> listAllAccounts();
    Page<AccountDto> listAccountsByKey(String keySearch, String status, int page, int size);
    BigDecimal getAccountBalance(UUID accountId);
}
