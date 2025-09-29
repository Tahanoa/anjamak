package org.example.anjamak.controller;

import org.example.anjamak.dto.ExceptionDto;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionController {
    private final MessageSourceAccessor messageSource;
    public ExceptionController(MessageSourceAccessor messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ExceptionDto>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<ExceptionDto> exceptionDtos = ex.getBindingResult()
                .getFieldErrors().stream()
                .map(error -> new ExceptionDto(error.getField(), messageSource.getMessage(error.getDefaultMessage())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(exceptionDtos);
    }
}
