package com.midel.exception;

import com.midel.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> defaultEntry(Exception e) {
        log.warn("", e);
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                e.getMessage()
        )
                .getResponseEntity();
    }

}
