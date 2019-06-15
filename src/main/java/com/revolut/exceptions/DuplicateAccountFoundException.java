package com.revolut.exceptions;

import lombok.Getter;

public class DuplicateAccountFoundException extends RuntimeException {

  @Getter
  private String message;

  public DuplicateAccountFoundException(String message) {
    super(message);
    this.message = message;
  }
}
