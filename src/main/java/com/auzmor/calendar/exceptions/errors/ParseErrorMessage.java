package com.auzmor.calendar.exceptions.errors;

import static com.auzmor.calendar.constants.ErrorConstant.EMAIL_DOESNT_EXISTS;

public class ParseErrorMessage {
  public static String parseErrorMessage(Exception exception) {
    String message = null;
    if (exception.getMessage().contains("404 NOT FOUND")) {
      message = EMAIL_DOESNT_EXISTS;
    }
    /*else if (exception.getMessage().contains("Column name cannot be null")) {
      message = ORGANIZATION_NAME_NOT_NULL;
    } else {
      message = FAILED;
    }

     */
    return message;
  }
}
