package com.auzmor.calendar.exceptions.errors;

import lombok.Data;

@Data
public class Error {

  private ErrorCode code;
  private ErrorType type;
  private String message; //TODO check This can be optional?

  public Error(final String message, final ErrorCode code, final ErrorType type) {
    this.setCode(code);
    this.setType(type);
    this.setMessage(message);
  }
}
