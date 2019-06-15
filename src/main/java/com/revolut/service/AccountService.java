package com.revolut.service;

import com.revolut.domain.Account;
import com.revolut.domain.Transfer;
import com.revolut.exceptions.AccountNotFoundException;
import com.revolut.exceptions.DuplicateAccountForTransferException;
import com.revolut.exceptions.DuplicateAccountFoundException;
import com.revolut.exceptions.InsufficientBalanceException;
import com.revolut.exceptions.InvalidAmountException;
import com.revolut.repo.AccountRepo;
import io.micronaut.context.annotation.Prototype;
import io.micronaut.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;

@Prototype
public class AccountService {

  private final AccountRepo accountRepo;

  public AccountService(@NotNull final AccountRepo accountRepo) {
    this.accountRepo = accountRepo;
  }

  public HttpResponse add(@NotNull final Account account) {
    try {
      Account savedAccount = accountRepo.save(account);
      return HttpResponse.created(savedAccount);
    } catch (RuntimeException e) {
      throw new DuplicateAccountFoundException("AccountNumber " + account.getAccountNumber() + " Already Exists");
    }

  }

  public HttpResponse findByAccountNumber(@NotNull final Long accountNumber) {
    Optional<Account> byAccountNumber = accountRepo.findByAccountNumber(accountNumber);
    if (byAccountNumber.isPresent()) {
      return HttpResponse.ok(byAccountNumber.get());
    } else {
      throw new AccountNotFoundException("Account not found");
    }
  }


  public HttpResponse doTransfer(@NotNull final Transfer transfer) {
    if (transfer.getFromAccount().equals(transfer.getToAccount())) {
      throw new DuplicateAccountForTransferException("Transfer Account cannot be same");
    } else if (transfer.getAmount().floatValue() <= 0.00) {
      throw new InvalidAmountException("Invalid Amount");
    } else {
      List<Account> accountNumbers = accountRepo
          .findByAccountNumbers(transfer.getFromAccount(), transfer.getToAccount());
      if (accountNumbers.size() < 2) {
        throw new AccountNotFoundException("One of the Account Numbers is Invalid");
      }
    }
    Optional<Account> balance = accountRepo
        .findInSufficientBalanceByAccountNumberAndAmount(transfer.getFromAccount(), transfer.getAmount());
    if (balance.isPresent()) {
      accountRepo.doTransfer(transfer.getFromAccount(), transfer.getToAccount(), transfer.getAmount());
      Optional<Account> fromAccount = accountRepo.findByAccountNumber(transfer.getFromAccount());
      return HttpResponse.ok(fromAccount.get());
    } else {
      throw new InsufficientBalanceException("Insufficient Balance in AccountNumber " + transfer.getFromAccount());
    }
  }
}

