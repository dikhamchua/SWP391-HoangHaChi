package com.kiotretail.shared.exception;

public class ServiceException extends RuntimeException {

    private int statusCode = 500;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getStatusCode() {
        return statusCode;
    }
}
