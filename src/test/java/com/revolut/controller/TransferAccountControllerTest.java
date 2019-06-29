package com.revolut.controller;

import static io.micronaut.http.HttpStatus.BAD_REQUEST;
import static io.micronaut.http.HttpStatus.OK;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import com.revolut.domain.Account;
import com.revolut.domain.Transfer;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;
import java.math.BigDecimal;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@MicronautTest
public class TransferAccountControllerTest {

  private final String SAME_ACCOUNT_NUMBER = "Transfer Account cannot be same";
  private final String INSUFFICIENT_BALANCE = "Insufficient Balance in AccountNumber 12345";
  private final String INVALID_AMOUNT = "Invalid Amount";
  private final String INVALID_ACCOUNT_NUMBER = "One of the Account Numbers is Invalid";
  @Inject
  private PlatformTransactionManager transactionManager;
  @Inject
  private AccountTestClient accountClient;
  @Inject
  private EntityManager entityManager;
  private Account firstUserAccount;
  private Account secondUserAccount;
  private Transfer accountTransfer;

  @BeforeEach
  public void setup() {
    firstUserAccount = Account.builder().name("FIRST_USER").accountNumber(12345L).amount(new BigDecimal(100.00))
        .build();
    secondUserAccount = Account.builder().name("SECOND_USER").accountNumber(123456L).amount(new BigDecimal(200.00))
        .build();
    final TransactionStatus tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
    entityManager.persist(firstUserAccount);
    entityManager.persist(secondUserAccount);
    transactionManager.commit(tx);
  }


  @Test
  void shouldTransferAmountFromOneAccountToAnotherAndReturnBalance() {
    // Given
    accountTransfer = Transfer.builder().fromAccount(firstUserAccount.getAccountNumber())
        .toAccount(secondUserAccount.getAccountNumber()).amount(new BigDecimal(20.67)).build();

    // Actual Call
    HttpResponse response = accountClient.doTransfer(accountTransfer);
    Account account = (Account) response.getBody(Account.class).orElse(null);

    // Assert
    assertThat(response.getStatus(), is(OK));
    assertNotNull(account);
    assertThat(account.getAccountNumber(), is(firstUserAccount.getAccountNumber()));
    assertThat(account.getAmount().toString(), is("79.33"));
  }

  @Test
  void shouldThrowAnExceptionForInsufficientBalance() {
    // Given
    accountTransfer = Transfer.builder().fromAccount(firstUserAccount.getAccountNumber())
        .toAccount(secondUserAccount.getAccountNumber()).amount(new BigDecimal(200.00)).build();

    // Actual Call
    try {
      HttpResponse response = accountClient.doTransfer(accountTransfer);
    } catch (HttpClientResponseException ex) {
      // Assert
      assertThat(ex.getStatus(), is(BAD_REQUEST));
      assertThat(ex.getMessage(), containsString(INSUFFICIENT_BALANCE));
    }
  }

  @Test
  void shouldThrowAnExceptionIfAccountNumbersAreSame() {
    // Given
    accountTransfer = Transfer.builder().fromAccount(firstUserAccount.getAccountNumber())
        .toAccount(firstUserAccount.getAccountNumber()).amount(new BigDecimal(100.00)).build();

    // Actual Call
    try {
      HttpResponse response = accountClient.doTransfer(accountTransfer);
    } catch (HttpClientResponseException ex) {
      // Assert
      assertThat(ex.getStatus(), is(BAD_REQUEST));
      assertThat(ex.getMessage(), containsString(SAME_ACCOUNT_NUMBER));
    }
  }

  @Test
  void shouldThrowAnExceptionIfTransferAmountIsZero() {
    // Given
    accountTransfer = Transfer.builder().fromAccount(firstUserAccount.getAccountNumber())
        .toAccount(secondUserAccount.getAccountNumber()).amount(new BigDecimal(0.00)).build();

    // Actual Call
    try {
      HttpResponse response = accountClient.doTransfer(accountTransfer);
    } catch (HttpClientResponseException ex) {
      // Assert
      assertThat(ex.getStatus(), is(BAD_REQUEST));
      assertThat(ex.getMessage(), containsString(INVALID_AMOUNT));
    }
  }

  @Test
  void shouldThrowAnExceptionIfTransferAmountIsNegative() {
    // Given
    accountTransfer = Transfer.builder().fromAccount(firstUserAccount.getAccountNumber())
        .toAccount(secondUserAccount.getAccountNumber()).amount(new BigDecimal(-0.00)).build();

    try {
      // Actual Call
      HttpResponse response = accountClient.doTransfer(accountTransfer);
    } catch (HttpClientResponseException ex) {
      // Assert
      assertThat(ex.getStatus(), is(BAD_REQUEST));
      assertThat(ex.getMessage(), containsString(INVALID_AMOUNT));
    }
  }

  @Test
  void shouldThrowAnExceptionIfOneOfTheAccountNumberIsInvalid() {
    // Given
    accountTransfer = Transfer.builder().fromAccount(firstUserAccount.getAccountNumber()).toAccount(000000L)
        .amount(new BigDecimal(10.00)).build();

    // Actual Call
    try {
      HttpResponse response = accountClient.doTransfer(accountTransfer);
    } catch (HttpClientResponseException ex) {
      // Assert
      assertThat(ex.getStatus(), is(BAD_REQUEST));
      assertThat(ex.getMessage(), containsString(INVALID_ACCOUNT_NUMBER));
    }
  }


  @AfterEach
  public void tearDown() {
    final TransactionStatus tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
    final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    final CriteriaDelete<Account> delete = criteriaBuilder.createCriteriaDelete(Account.class);
    delete.from(Account.class);
    entityManager.createQuery(delete).executeUpdate();
    transactionManager.commit(tx);
  }

}
