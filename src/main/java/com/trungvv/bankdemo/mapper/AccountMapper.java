package com.trungvv.bankdemo.mapper;

import com.trungvv.bankdemo.dto.AccountDto;
import com.trungvv.bankdemo.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    AccountDto accountToAccountDto(Account account);
    Account accountDtoToAccount(AccountDto accountDto);
    List<AccountDto> accountsToAccountDtos(List<Account> accounts);
    List<Account> accountDtosToAccounts(List<AccountDto> accountDtos);
}