package com.security.demo.exceptions;

import com.security.demo.constants.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@RestControllerAdvice
public class ExceptionHandler  extends ResponseEntityExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(DataIntegrityException.class)
    public final ResponseEntity<Object> handleInvalidFileExceptions(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), Constants.DATA_INTEGRITY_EXCEPTION.replace("{}",ex.getMessage()));
        logger.error("Invalid File Exceptions => ",ex);
        return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
