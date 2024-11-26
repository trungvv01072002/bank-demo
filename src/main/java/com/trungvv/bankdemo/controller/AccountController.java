package com.trungvv.bankdemo.controller;

import com.trungvv.bankdemo.dto.AccountDto;
import com.trungvv.bankdemo.service.AccountService;
import com.trungvv.bankdemo.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;

    // Tạo tài khoản
    @PostMapping("/")
    public ResponseEntity<AccountDto> createAccount(@RequestParam String name, @RequestParam BigDecimal initialBalance) {
        AccountDto newAccount = accountService.createAccount(name, initialBalance);
        return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
    }

    // Lấy tài khoản theo ID
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable String accountId) {
        try {
            AccountDto account = accountService.getAccountById(UUID.fromString(accountId));
            return new ResponseEntity<>(account, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Cập nhật trạng thái tài khoản
    @PutMapping("/{accountId}/status")
    public ResponseEntity<AccountDto> updateAccountStatus(@PathVariable String accountId, @RequestParam String status) {
        try {
            AccountDto updatedAccount = accountService.updateAccountStatus(UUID.fromString(accountId), status);
            return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Cập nhật số dư tài khoản
    @PutMapping("/{accountId}/balance")
    public ResponseEntity<AccountDto> updateAccountBalance(@PathVariable String accountId, @RequestParam BigDecimal newBalance) {
        try {
            AccountDto updatedAccount = accountService.updateAccountBalance(UUID.fromString(accountId), newBalance);
            return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Xóa tài khoản
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String accountId) {
        try {
            accountService.deleteAccount(UUID.fromString(accountId));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Lấy danh sách tất cả tài khoản
    @GetMapping("/")
    public ResponseEntity<List<AccountDto>> listAllAccounts() {
        List<AccountDto> accounts = accountService.listAllAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    // Lọc tài khoản theo trạng thái
    @GetMapping("/status")
    public ResponseEntity<List<AccountDto>> listAccountsByStatus(@RequestParam String status) {
        List<AccountDto> accounts = accountService.listAccountsByStatus(status);
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    // Lấy số dư của tài khoản
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BigDecimal> getAccountBalance(@PathVariable String accountId) {
        try {
            BigDecimal balance = accountService.getAccountBalance(UUID.fromString(accountId));
            return new ResponseEntity<>(balance, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

//    @GetMapping
//    public ResponseEntity<AccountDto> getAccountByAccountNumber(@RequestParam String accountNumber) {
//        try {
//            AccountDto account = accountService.getAccountByAccountNumber(accountNumber);
//            return new ResponseEntity<>(account, HttpStatus.OK);
//        } catch (ResourceNotFoundException ex) {
//            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
//        }
//    }

    @GetMapping
    public ResponseEntity<?> getALLAccount() {
        return ResponseEntity.ok(accountService.listAllAccounts());
    }
}
