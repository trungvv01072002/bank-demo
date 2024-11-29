package com.trungvv.bankdemo.mapper;

import com.trungvv.bankdemo.dto.TransactionDto;
import com.trungvv.bankdemo.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mapping(source = "transaction.message", target = "description")
    @Mapping(source = "transaction.status", target = "status")
     TransactionDto transactionToTransactionDto(Transaction transaction);
//     Transaction transactionDtoToTransaction(TransactionDto transactionDto);
     List<TransactionDto> transactionsToTransactionDtos(List<Transaction> transactions);
//     List<Transaction> transactionDtosToTransactions(List<TransactionDto> transactionDtos);
}
