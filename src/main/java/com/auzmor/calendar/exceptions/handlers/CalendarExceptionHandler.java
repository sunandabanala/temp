package com.auzmor.calendar.exceptions.handlers;


import com.auzmor.calendar.exceptions.CalendarException;
import com.auzmor.calendar.exceptions.DBException;
import com.auzmor.calendar.exceptions.errors.Error;
import com.auzmor.calendar.exceptions.errors.ErrorCode;
import com.auzmor.calendar.exceptions.errors.ErrorType;
import liquibase.util.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import static com.auzmor.calendar.exceptions.errors.ParseErrorMessage.parseErrorMessage;

@ControllerAdvice
public class CalendarExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(CalendarExceptionHandler.class);

  @ExceptionHandler({
    Exception.class,
    DBException.class,
    CalendarException.class,
    DataIntegrityViolationException.class,
    SQLException.class,
    PersistenceException.class
  })
  public final ResponseEntity<Object> handleAllExceptions(Exception exception, WebRequest request) {
    logger.error("Exception--", exception.getCause());
    Error error = null;
    List<Error> errors = new ArrayList<>();
    request.getDescription(false);
    exception.printStackTrace();
    if (exception instanceof DBException) {
      errors = ((DBException) exception).getErrors();
    } else if (exception instanceof DataIntegrityViolationException) {
      if (exception.getCause() instanceof SQLIntegrityConstraintViolationException) {
        error = new Error(parseErrorMessage((SQLException)exception.getCause()), ErrorCode.E3000004, ErrorType.FAILED);
      } else {
        error = new Error(parseErrorMessage(exception), ErrorCode.E3000004, ErrorType.FAILED);
      }
      errors.add(error);
    } else if(exception instanceof DuplicateKeyException) {
      if (exception.getCause() instanceof SQLIntegrityConstraintViolationException) {
        error = new Error(parseErrorMessage((SQLException)exception.getCause()), ErrorCode.E3000004, ErrorType.DUPLICATE_ACTION);
      } else {
        error = new Error("Already exists", ErrorCode.E3000004, ErrorType.DUPLICATE_ACTION);
      }
      errors.add(error);
    } else if(exception instanceof DataIntegrityViolationException) {
      if (exception.getMessage().contains("ORG_EMAIL_UNIQUE")) {
        error = new Error("ORG_EMAIL_UNIQUE_MSG", ErrorCode.E3000004, ErrorType.DUPLICATE_ACTION);
        errors.add(error);
      }
    }
    else if (exception instanceof InsufficientAuthenticationException) {
      InsufficientAuthenticationException ex = (InsufficientAuthenticationException) exception;
      error = getAuthError(ex);
      errors.add(error);
    } else if (exception instanceof DataIntegrityViolationException) {
      DataIntegrityViolationException ex = (DataIntegrityViolationException) exception;
      error = new Error(parseErrorMessage(exception), ErrorCode.E3000004, ErrorType.DATA_INTEGRITY);
      errors.add(error);
    } else if (exception instanceof CalendarException) {
      errors = ((CalendarException) exception).getErrors();
    }
    else if (exception instanceof HttpClientErrorException) {
      System.out.println(exception.getMessage());
      HttpClientErrorException ex = (HttpClientErrorException) exception;
      error = new Error(parseErrorMessage(ex), ErrorCode.E3000004, ErrorType.NOT_EXISTS);
      errors.add(error);
    }
    if (errors != null && ! errors.isEmpty()) {
      return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST); //TODO check proper error status
    }
    Error defaultErrors = new Error("DEFAULT_ERROR_MSG", ErrorCode.E0000000, ErrorType.INTERNAL_SERVER_ERROR);
    errors.add(defaultErrors);
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                HttpHeaders headers, HttpStatus status,
                                                                WebRequest request) {
    List<Error> errors = new ArrayList<>();
    for(ObjectError e : ex.getBindingResult().getAllErrors()){
      logger.error("Error Message : "+ ex.getLocalizedMessage());
      logger.error("Error Code : "+ e.getCode()); //TODO auto generate Error code based on field and Error Type
      logger.error("Error Cause : "+ ex.getCause());
      logger.error("Error request : "+ request);
      Error error = new Error(e.getDefaultMessage(), ErrorCode.E3000004, ErrorType.INVALID_PARAMETER);
      errors.add(error);
    }
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  private Error getAuthError(InsufficientAuthenticationException e) {
    logger.error(e.getMessage());
    Error error = null;
    final String errorMessage = e.getMessage();
    if (StringUtils.startsWith(errorMessage, "Access token expired")) {
      error = new Error(null, ErrorCode.E3000004, ErrorType.AUTH);
    } else if (StringUtils.startsWith(errorMessage, "Refresh token expired")) {
      error = new Error(null, ErrorCode.E3000004, ErrorType.AUTH);
    } else {
      error = new Error(null, ErrorCode.E3000004, ErrorType.AUTH);
    }
    return error;
  }
}
