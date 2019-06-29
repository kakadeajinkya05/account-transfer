package com.revolut.service;

import static org.mockito.Mockito.when;

import com.revolut.domain.Transfer;
import com.revolut.exceptions.DuplicateAccountForTransferException;
import com.revolut.repo.AccountRepo;
import io.micronaut.http.HttpResponse;
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

  @InjectMocks
  AccountService accountService;
  @Mock
  AccountRepo accountRepo;

  private Transfer transfer;

  @Before
  public void setup() {
    transfer = Transfer.builder().fromAccount(12345L).toAccount(12345L).amount(new BigDecimal(123)).build();
  }

  @Test(expected = DuplicateAccountForTransferException.class)
  public void shouldThrowAnExceptionIfAccountNumberAreSame() {
    when(accountService.doTransfer(transfer)).thenReturn(HttpResponse.ok(transfer));
  }
}
