package com.revolut.controller;

import com.revolut.domain.Account;
import com.revolut.domain.Transfer;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface AccountClient {

  @Post("/add")
  public HttpResponse addAccount(@Body @Valid Account account);

  @Get("/{accountId}")
  public HttpResponse getAccount(@Body @NotNull Long accountId);

  @Post("/transfer")
  public HttpResponse doTransfer(@Body @Valid Transfer transfer);

}
