package com.trungvv.bankdemo.controller;

import com.trungvv.bankdemo.dto.AccountDto;
import com.trungvv.bankdemo.exception.ResourceNotFoundException;
import com.trungvv.bankdemo.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    private AccountDto accountDto;
    private UUID accountId;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        accountDto = AccountDto.builder()
                .id(accountId)
                .accountNumber("12345678")
                .accountName("Test Account")
                .balance(BigDecimal.valueOf(1000))
                .status("ACTIVE")
                .build();
    }

    @Test
    void createAccount_ShouldReturnCreatedAccount() throws Exception {
        when(accountService.createAccount(anyString(), any(BigDecimal.class))).thenReturn(accountDto);

        ResultActions result = mockMvc.perform(post("/api/accounts/")
                .param("name", "Test Account")
                .param("initialBalance", "1000")
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(accountId.toString()))
                .andExpect(jsonPath("$.accountName").value("Test Account"))
                .andExpect(jsonPath("$.balance").value(1000))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(accountService, times(1)).createAccount("Test Account", BigDecimal.valueOf(1000));
    }

    @Test
    void getAccountById_ShouldReturnAccount_WhenAccountExists() throws Exception {
        when(accountService.getAccountById(accountId)).thenReturn(accountDto);

        ResultActions result = mockMvc.perform(get("/api/accounts/{accountId}", accountId.toString())
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId.toString()))
                .andExpect(jsonPath("$.accountName").value("Test Account"))
                .andExpect(jsonPath("$.balance").value(1000))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(accountService, times(1)).getAccountById(accountId);
    }

    @Test
    void getAccountById_ShouldReturnNotFound_WhenAccountDoesNotExist() throws Exception {
        when(accountService.getAccountById(accountId)).thenThrow(new ResourceNotFoundException("Account not found"));

        ResultActions result = mockMvc.perform(get("/api/accounts/{accountId}", accountId.toString())
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
        verify(accountService, times(1)).getAccountById(accountId);
    }

    @Test
    void deleteAccount_ShouldReturnNoContent() throws Exception {
        ResultActions result = mockMvc.perform(delete("/api/accounts/{accountId}", accountId.toString())
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
        verify(accountService, times(1)).deleteAccount(accountId);
    }

    @Test
    void listAllAccounts_ShouldReturnListOfAccounts() throws Exception {
        when(accountService.listAllAccounts()).thenReturn(List.of(accountDto));

        ResultActions result = mockMvc.perform(get("/api/accounts/")
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(accountId.toString()))
                .andExpect(jsonPath("$[0].accountName").value("Test Account"))
                .andExpect(jsonPath("$[0].balance").value(1000))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(accountService, times(1)).listAllAccounts();
    }

    @Test
    void listAllAccounts_ShouldReturnEmptyList_WhenNoAccountsExist() throws Exception {
        when(accountService.listAllAccounts()).thenReturn(Collections.emptyList());

        ResultActions result = mockMvc.perform(get("/api/accounts/")
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(accountService, times(1)).listAllAccounts();
    }

    @Test
    void listAccountsByStatus_ShouldReturnAccounts_WhenValidRequest() throws Exception {
        String keySearch = "Test";
        String status = "ACTIVE";
        int page = 0;
        int size = 5;

        PageImpl<AccountDto> accountPage = new PageImpl<>(List.of(accountDto));

        when(accountService.listAccountsByKey(keySearch, status, page, size)).thenReturn(accountPage);

        ResultActions result = mockMvc.perform(get("/api/accounts/search")
                .param("keySearch", keySearch)
                .param("status", status)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].accountName").value("Test Account"))
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(accountService, times(1)).listAccountsByKey(keySearch, status, page, size);
    }

    @Test
    void listAccountsBySearch_ShouldReturnEmpty_WhenNoResults() throws Exception {
        String keySearch = "NonExisting";
        String status = "INACTIVE";
        int page = 0;
        int size = 5;

        PageImpl<AccountDto> emptyPage = new PageImpl<>(Collections.emptyList());
        when(accountService.listAccountsByKey(keySearch, status, page, size)).thenReturn(emptyPage);

        ResultActions result = mockMvc.perform(get("/api/accounts/search")
                .param("keySearch", keySearch)
                .param("status", status)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(accountService, times(1)).listAccountsByKey(keySearch, status, page, size);
    }

}
