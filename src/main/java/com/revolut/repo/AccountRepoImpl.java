package com.revolut.repo;

import com.revolut.domain.Account;
import io.micronaut.configuration.hibernate.jpa.scope.CurrentSession;
import io.micronaut.spring.tx.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

@Singleton
public class AccountRepoImpl implements AccountRepo {

  @PersistenceContext
  private EntityManager entityManager;

  public AccountRepoImpl(@CurrentSession EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Account> findByAccountNumber(@NotNull Long accountNumber) {
    String queryString = "SELECT a FROM Account as a WHERE a.accountNumber = :accNo";
    TypedQuery<Account> query = entityManager.createQuery(queryString, Account.class)
        .setParameter("accNo", accountNumber).setLockMode(LockModeType.PESSIMISTIC_READ);
    Account account = query.getResultList().isEmpty() ? null : query.getResultList().get(0);
    return Optional.ofNullable(account);
  }

  @Override
  @Transactional
  public Optional<Account> findInSufficientBalanceByAccountNumberAndAmount(@NotNull Long accountNumber,
      @NotNull BigDecimal amount) {
    String queryString = "SELECT a FROM Account as a WHERE a.accountNumber = :accNo AND (a.amount - :amt) >= 0";
    TypedQuery<Account> query = entityManager.createQuery(queryString, Account.class)
        .setParameter("accNo", accountNumber).setParameter("amt", amount).setLockMode(LockModeType.PESSIMISTIC_READ);
    Account account = query.getResultList().isEmpty() ? null : query.getResultList().get(0);
    return Optional.ofNullable(account);
  }

  @Override
  @Transactional
  public List<Account> findByAccountNumbers(@NotNull Long accountNumber1, @NotNull Long accountNumber2) {
    String queryString = "SELECT a FROM Account as a WHERE a.accountNumber in (:accNo1,:accNo2)";
    TypedQuery<Account> query = entityManager.createQuery(queryString, Account.class)
        .setParameter("accNo1", accountNumber1).setParameter("accNo2", accountNumber2)
        .setLockMode(LockModeType.PESSIMISTIC_READ);
    return query.getResultList();
  }

  @Override
  @Transactional
  public void doTransfer(@NotNull Long fromAccount, @NotNull Long toAccount, @NotNull BigDecimal amount) {
    String queryString = "UPDATE ACCOUNTS SET AMOUNT = CASE WHEN ACCOUNT_NUMBER = :fromAcc THEN AMOUNT - :amount  WHEN ACCOUNT_NUMBER = :toAcc THEN AMOUNT + :amount ELSE AMOUNT END;";
    entityManager.createNativeQuery(queryString, Account.class).setParameter("amount", amount)
        .setParameter("fromAcc", fromAccount).setParameter("toAcc", toAccount).executeUpdate();
  }

  @Override
  @Transactional(rollbackFor = RuntimeException.class)
  public Account save(@NotNull Account account) throws RuntimeException {
    entityManager.persist(account);
    return account;
  }
}
