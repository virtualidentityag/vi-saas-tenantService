package com.vi.tenantservice.api.controller;

import com.vi.tenantservice.api.exception.TenantValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {TenantValidationException.class})
  protected ResponseEntity<Object> handle(RuntimeException ex, WebRequest request) {

    var customHttpHeader = ((TenantValidationException) ex).getCustomHttpHeaders();
    return handleExceptionInternal(ex, "", customHttpHeader, HttpStatus.CONFLICT, request);
  }

  @ExceptionHandler(value = {IllegalStateException.class})
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  protected void handleIllegalStateException() {
    // status code is set with ResponseStatus
  }
}