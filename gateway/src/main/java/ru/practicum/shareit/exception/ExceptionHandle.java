package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class ExceptionHandle {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(final MethodArgumentTypeMismatchException e) {
        String error = "Unknown " + e.getName() + ": " + e.getValue();
        log.error(error);
        return ResponseEntity.status(400).body(new ErrorResponse(error));
    }
}
