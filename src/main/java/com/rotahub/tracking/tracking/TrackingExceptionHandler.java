package com.rotahub.tracking.tracking;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TrackingExceptionHandler {

    @ExceptionHandler(TrackingNotFoundException.class)
    public ResponseEntity<Void> handleNotFound(TrackingNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Void> handleDuplicate(DuplicateKeyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}
