package com.kiotretail.shared.exception;

import java.util.Map;

public class ValidationException extends ServiceException {

    private Map<String, String> fieldErrors;

    public ValidationException(String message) {
        super(message, 400);
    }

    public ValidationException(Map<String, String> fieldErrors) {
        super("Validation failed", 400);
        this.fieldErrors = fieldErrors;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}
