package com.ljuslin.wigellMCRental.exception;

import com.ljuslin.wigellMCRental.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ItemNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalDataException.class)
    public ResponseEntity<ErrorResponse> handleIllegalData(IllegalDataException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
    }

    @ExceptionHandler(DataConflictException.class)
    public ResponseEntity<ErrorResponse> handleDataConflict(DataConflictException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalActionException.class)
    public ResponseEntity<ErrorResponse> handleIllegalAction(IllegalActionException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Illegal Action", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation Error", errorMessage, request);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, HttpServletRequest request) {
        logger.error("Unexpected system error: ", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Something went wrong", request);
    }
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status,
            String errorTitle,
            String message,
            HttpServletRequest request) {

        String path = request.getRequestURI();

        logger.error("{} ({}): {} at {}", errorTitle, status.value(), message, path);

        ErrorResponse errorBody = new ErrorResponse(
                LocalDateTime.now().format(formatter),
                status.value(),
                errorTitle,
                message,
                path
        );

        return new ResponseEntity<>(errorBody, status);
    }
}