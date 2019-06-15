package com.revolut.controller;

import static io.micronaut.http.HttpStatus.CREATED;
import static io.micronaut.http.HttpStatus.OK;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import com.revolut.domain.Account;
import com.revolut.domain.ResponseType;
import io.micronaut.http.HttpResponse;
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
public class CreateAccountControllerTest {

  private static final String ACCOUNT_NUMBER_EXISTS = "AccountNumber 12345 Already Exists";

  @Inject
  private PlatformTransactionManager transactionManager;
  @Inject
  private AccountTestClient accountClient;
  @Inject
  private EntityManager entityManager;
  private Account firstUserAccount;
  private Account secondUserAccount;

  @BeforeEach
  public void setup() {
    firstUserAccount = Account.builder().name("FIRST_USER").accountNumber(12345L).amount(new BigDecimal(120.00))
        .build();
    secondUserAccount = Account.builder().name("SECOND_USER").accountNumber(123456L).amount(new BigDecimal(20.00))
        .build();
  }

  @Test
  void shouldCreateAccounts() {

    // Actual Call
    HttpResponse response = accountClient.addAccount(firstUserAccount);
    Account account = (Account) response.getBody(Account.class).orElse(null);
    // Assert
    assertThat(response.getStatus(), is(CREATED));
    assertNotNull(account);
    assertThat(account.getAccountNumber(), is(firstUserAccount.getAccountNumber()));

    // Actual Call
    response = accountClient.addAccount(secondUserAccount);
    account = (Account) response.getBody(Account.class).orElse(null);
    // Assert
    assertThat(response.getStatus(), is(CREATED));
    assertNotNull(account);
    assertThat(account.getAccountNumber(), is(secondUserAccount.getAccountNumber()));

  }

  @Test
  public void ShouldThrowAnExceptionIfAccountAlreadyExists() {

    // Actual Call
    HttpResponse response = accountClient.addAccount(firstUserAccount);
    Account account = (Account) response.getBody(Account.class).orElse(null);
    // Assert
    assertThat(response.getStatus(), is(CREATED));
    assertNotNull(account);
    assertThat(account.getAccountNumber(), is(firstUserAccount.getAccountNumber()));

    // Actual Call
    response = accountClient.addAccount(firstUserAccount);
    ResponseType accountResponseWireType = (ResponseType) response.getBody(ResponseType.class).orElse(null);
    // Assert
    assertThat(response.getStatus(), is(OK));
    assertThat(accountResponseWireType.getMessage(), is(ACCOUNT_NUMBER_EXISTS));
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
