package com.revolut.controller;

import com.revolut.domain.Account;
import com.revolut.domain.Transfer;
import com.revolut.service.AccountService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Controller("/account")
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class AccountController implements AccountClient {

  private final AccountService accountService;

  public AccountController(final AccountService accountService) {
    this.accountService = accountService;
  }

  @Override
  @Post("/add")
  public HttpResponse addAccount(@Body @Valid Account account) {
    return accountService.add(account);
  }

  @Override
  @Get("/{accountId}")
  public HttpResponse getAccount(@NotNull Long accountId) {
    return accountService.findByAccountNumber(accountId);
  }

  @Override
  @Post("/transfer")
  public HttpResponse doTransfer(@Body @Valid Transfer transfer) {
    return accountService.doTransfer(transfer);
  }


}