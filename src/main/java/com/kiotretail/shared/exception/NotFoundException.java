package com.kiotretail.shared.exception;

public class NotFoundException extends ServiceException {

    public NotFoundException(String entity, Object id) {
        super(entity + " not found: " + id, 404);
    }
}
