package com.revolut.exceptions;

import lombok.Getter;

public class InvalidAmountException extends RuntimeException {

  @Getter
  private String message;

  public InvalidAmountException(String message) {
    super(message);
    this.message = message;
  }
}
