package com.akash.supplychain.monitoring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    ProblemDetail handleValidation(WebExchangeBindException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Validation failed");
        problem.setDetail("One or more request fields are invalid.");
        problem.setType(URI.create("https://supply-chain-agentic-ai/problems/validation"));

        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(ServerWebInputException.class)
    ProblemDetail handleBadInput(ServerWebInputException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getReason());
        problem.setTitle("Invalid request body");
        problem.setType(URI.create("https://supply-chain-agentic-ai/problems/invalid-request-body"));
        return problem;
    }

    @ExceptionHandler(ResponseStatusException.class)
    ProblemDetail handleStatus(ResponseStatusException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(exception.getStatusCode(), exception.getReason());
        problem.setTitle(exception.getStatusCode().toString());
        return problem;
    }
}
