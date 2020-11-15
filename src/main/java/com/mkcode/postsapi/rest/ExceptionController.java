package com.mkcode.postsapi.rest;

import com.mkcode.postsapi.service.ExpressionValidationException;
import com.mkcode.postsapi.service.InvalidParameterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({ExpressionValidationException.class, InvalidParameterException.class})
    public String handleException(RuntimeException exception) {
        log.debug("Query validation exception happens: {}", exception.getMessage());
        return exception.getMessage();
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleException(MethodArgumentNotValidException exception) {
        log.debug("Method arguments validation exception happens: {}", exception.getMessage());
        return exception.getMessage();
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String handleException(Exception exception) {
        String msg = "Internal server error";
        log.error(msg, exception);
        return msg;
    }
}
