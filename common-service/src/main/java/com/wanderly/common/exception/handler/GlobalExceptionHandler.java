package com.wanderly.common.exception.handler;

import com.wanderly.common.dto.CustomResponse;
import com.wanderly.common.exception.*;
import com.wanderly.common.util.ResponseFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.validation.FieldError;

import java.util.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<CustomResponse<?>> handleEmailRequestLimit(RateLimitException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ResponseFactory.error(ex.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseFactory.error("Validation failed!", errors));
    }

    @ExceptionHandler({
            BadRequestException.class,

            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            MissingPathVariableException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<CustomResponse<?>> handleBadRequestException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseFactory.error(ex.getMessage(), null));
    }

    @ExceptionHandler({
            NotFoundException.class
    })
    public ResponseEntity<CustomResponse<?>> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.error(ex.getMessage(), null));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<CustomResponse<?>> handleUnauthorizedException(UnauthorizedException ignored) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseFactory.error("Unauthorized access", null));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<CustomResponse<?>> handleForbiddenException(ForbiddenException ignored) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseFactory.error("Access is denied", null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse<?>> handleException(Exception ex) {
        log.info("Unhandled exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseFactory.error(ex.getMessage(), null));
    }
}
