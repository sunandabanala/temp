package com.auzmor.calendar.exceptions;

import com.auzmor.calendar.exceptions.errors.Error;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DBException extends RuntimeException {
  private Error error;
  private List<Error> errors;

  public DBException(final Exception ex, final Error error) {
    super(error.getMessage());
  }

  public DBException(final Exception ex, final List<Error> errors) {
    this.errors = errors;
  }
}
