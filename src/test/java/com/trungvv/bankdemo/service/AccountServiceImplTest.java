package com.trungvv.bankdemo.service;

import com.trungvv.bankdemo.dto.AccountDto;
import com.trungvv.bankdemo.mapper.AccountMapper;
import com.trungvv.bankdemo.model.Account;
import com.trungvv.bankdemo.model.AccountStatus;
import com.trungvv.bankdemo.repository.AccountRepository;
import com.trungvv.bankdemo.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    private Account account;
    private AccountDto accountDto;
    private UUID accountId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accountId = UUID.randomUUID();
        account = Account.builder()
                .id(accountId)
                .accountNumber("12345678")
                .accountName("Test Account")
                .balance(BigDecimal.valueOf(1000))
                .status(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        accountDto = AccountDto.builder()
                .id(accountId)
                .accountNumber("12345678")
                .accountName("Test Account")
                .balance(BigDecimal.valueOf(1000))
                .status("ACTIVE")
                .build();
    }

    @Test
    void createAccount_ShouldReturnCreatedAccount() {
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(accountMapper.accountToAccountDto(any(Account.class))).thenReturn(accountDto);

        AccountDto result = accountService.createAccount("Test Account", BigDecimal.valueOf(1000));

        assertNotNull(result);
        assertEquals("Test Account", result.getAccountName());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void getAccountById_ShouldReturnAccount_WhenAccountExists() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMapper.accountToAccountDto(account)).thenReturn(accountDto);

        AccountDto result = accountService.getAccountById(accountId);

        assertNotNull(result);
        assertEquals(accountId, result.getId());
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void getAccountById_ShouldThrowException_WhenAccountNotFound() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountById(accountId));
    }

    @Test
    void updateAccountStatus_ShouldUpdateStatus_WhenValidStatusProvided() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(accountMapper.accountToAccountDto(account)).thenReturn(accountDto);

        AccountDto result = accountService.updateAccountStatus(accountId, AccountStatus.BLOCKED .toString());

        assertNotNull(result);
        assertEquals("BLOCKED", account.getStatus().toString());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void updateAccountBalance_ShouldUpdateBalance_WhenAccountExists() {
        BigDecimal newBalance = BigDecimal.valueOf(2000);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.accountToAccountDto(account)).thenReturn(accountDto);

        AccountDto result = accountService.updateAccountBalance(accountId, newBalance);

        assertNotNull(result);
        assertEquals(newBalance, account.getBalance());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void deleteAccount_ShouldRemoveAccount_WhenAccountExists() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountService.deleteAccount(accountId);

        verify(accountRepository, times(1)).delete(account);
    }

    @Test
    void listAllAccounts_ShouldReturnAllAccounts() {
        List<Account> accounts = List.of(account);
        List<AccountDto> accountDtos = List.of(accountDto);
        when(accountRepository.findAll()).thenReturn(accounts);
        when(accountMapper.accountsToAccountDtos(accounts)).thenReturn(accountDtos);

        List<AccountDto> result = accountService.listAllAccounts();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(accountRepository, times(1)).findAll();
    }
}
