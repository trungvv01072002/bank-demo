package com.trungvv.bankdemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trungvv.bankdemo.dto.TransactionDto;
import com.trungvv.bankdemo.model.Transaction;
import com.trungvv.bankdemo.model.TransactionStatus;
import com.trungvv.bankdemo.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTransaction_ShouldReturnCreatedTransaction() throws Exception {
        TransactionDto transactionDto = new TransactionDto(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), BigDecimal.valueOf(1000), "SUCCESS", "Test Message");

        Mockito.when(transactionService.createTransaction(any(UUID.class), any(UUID.class), any(BigDecimal.class), any(String.class)))
                .thenReturn(transactionDto);

        mockMvc.perform(post("/api/transactions")
                        .param("senderId", UUID.randomUUID().toString())
                        .param("receiverId", UUID.randomUUID().toString())
                        .param("amount", "1000")
                        .param("message", "Test Message")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description", is("Test Message")))
                .andExpect(jsonPath("$.amount", is(1000)));
    }

    @Test
    void getTransactionById_ShouldReturnTransaction() throws Exception {
        UUID transactionId = UUID.randomUUID();
        Transaction transaction = Transaction.builder()
                .id(transactionId)
                .senderAccountId(UUID.randomUUID())
                .receiverAccountId(UUID.randomUUID())
                .amount(BigDecimal.valueOf(500))
                .createdAt(LocalDate.now().atStartOfDay())
                .status(TransactionStatus.SUCCESS)
                .build();

        Mockito.when(transactionService.getTransactionById(eq(transactionId))).thenReturn(transaction);

        mockMvc.perform(get("/api/transactions/{transactionId}", transactionId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(500)))
                .andExpect(jsonPath("$.status", is("SUCCESS")));
    }

    @Test
    void updateTransactionStatus_ShouldUpdateAndReturnTransaction() throws Exception {
        UUID transactionId = UUID.randomUUID();
        TransactionDto transactionDto = new TransactionDto(transactionId, UUID.randomUUID(), UUID.randomUUID(), BigDecimal.valueOf(1000), "COMPLETED", "Test Message");

        Mockito.when(transactionService.updateTransactionStatus(eq(transactionId), eq("COMPLETED"))).thenReturn(transactionDto);

        mockMvc.perform(put("/api/transactions/{transactionId}/status", transactionId.toString())
                        .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("COMPLETED")));
    }

    @Test
    void deleteTransaction_ShouldReturnNoContent() throws Exception {
        UUID transactionId = UUID.randomUUID();

        Mockito.doNothing().when(transactionService).deleteTransaction(eq(transactionId));

        mockMvc.perform(delete("/api/transactions/{transactionId}", transactionId.toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    void listTransactionsByUserId_ShouldReturnListOfTransactions() throws Exception {
        UUID userId = UUID.randomUUID();
        List<TransactionDto> transactions = List.of(
                new TransactionDto(UUID.randomUUID(), userId, UUID.randomUUID(), BigDecimal.valueOf(500), "Test Message 1", "SUCCESS"),
                new TransactionDto(UUID.randomUUID(), UUID.randomUUID(), userId, BigDecimal.valueOf(1000), "Test Message 2", "SUCCESS")
        );

        Mockito.when(transactionService.listTransactionsByUserId(eq(userId))).thenReturn(transactions);

        mockMvc.perform(get("/api/transactions/user/{userId}", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].amount", is(500)))
                .andExpect(jsonPath("$[1].amount", is(1000)));
    }

    @Test
    void listTransactionsByDateRange_ShouldReturnPagedTransactions() throws Exception {
        UUID accountId = UUID.randomUUID();
        List<TransactionDto> transactions = List.of(
                new TransactionDto(UUID.randomUUID(), accountId, UUID.randomUUID(), BigDecimal.valueOf(2000), "Test", "SUCCESS")
        );

        Mockito.when(transactionService.listTransactionsByDateRange(any(LocalDate.class), any(LocalDate.class), eq(accountId), eq("SUCCESS"),eq("ALL"), eq(0), eq(10)))
                .thenReturn(Page.empty());

        mockMvc.perform(get("/api/transactions/date-range")
                        .param("startDate", LocalDate.now().minusDays(1).toString())
                        .param("endDate", LocalDate.now().toString())
                        .param("accountId", accountId.toString())
                        .param("status", "SUCCESS")
                        .param("typeTransaction", "ALL")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size", is(0)));
    }

    @Test
    void getTotalTransactionsAmountByAccountId_ShouldReturnTotalAmount() throws Exception {
        UUID accountId = UUID.randomUUID();
        BigDecimal totalAmount = BigDecimal.valueOf(3000);

        Mockito.when(transactionService.getTotalTransactionsAmountByAccountId(eq(accountId))).thenReturn(totalAmount);

        mockMvc.perform(get("/api/transactions/total-amount/{accountId}", accountId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("3000"));
    }

    @Test
    void getDailyTransactionSummary_ShouldReturnSummary() throws Exception {
        LocalDate date = LocalDate.now();

        Mockito.when(transactionService.getDailyTransactionSummary(eq(date))).thenReturn(List.of());

        mockMvc.perform(get("/api/transactions/daily-summary")
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
