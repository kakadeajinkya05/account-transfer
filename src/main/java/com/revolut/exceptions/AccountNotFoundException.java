package com.revolut.exceptions;

import lombok.Getter;

public class AccountNotFoundException extends RuntimeException {

  @Getter
  private String message;

  public AccountNotFoundException(String message) {
    this.message = message;
  }
}
