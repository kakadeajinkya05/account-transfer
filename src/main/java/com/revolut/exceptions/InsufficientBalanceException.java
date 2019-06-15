package com.revolut.exceptions;

import lombok.Getter;

public class InsufficientBalanceException extends RuntimeException {

  @Getter
  private String message;

  public InsufficientBalanceException(String message) {
    super(message);
    this.message = message;
  }
}