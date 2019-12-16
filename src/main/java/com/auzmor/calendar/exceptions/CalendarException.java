package com.auzmor.calendar.exceptions;

import com.auzmor.calendar.exceptions.errors.Error;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CalendarException extends Exception {

  public Error getError() {
    return error;
  }
  protected Error error;
  protected List<Error> errors;
  protected Exception exception;

  public CalendarException (final Exception ex, final List<Error> errors) {
    this.errors = errors;
  }
}
