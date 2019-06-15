package com.revolut.exceptions;

import lombok.Getter;

public class DuplicateAccountForTransferException extends RuntimeException {

  @Getter
  private String message;

  public DuplicateAccountForTransferException(String message) {
    super(message);
    this.message = message;
  }
}
