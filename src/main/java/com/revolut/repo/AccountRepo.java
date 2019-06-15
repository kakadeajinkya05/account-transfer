package com.revolut.repo;

import com.revolut.domain.Account;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;

public interface AccountRepo {

  Optional<Account> findByAccountNumber(Long accountNumber);

  Optional<Account> findInSufficientBalanceByAccountNumberAndAmount(Long accountNumber, BigDecimal amount);

  List<Account> findByAccountNumbers(Long accountNumber1, Long accountNumber2);

  void doTransfer(Long fromAccount, Long toAccount, BigDecimal amount);

  Account save(@NotNull Account account) throws RuntimeException;
}
