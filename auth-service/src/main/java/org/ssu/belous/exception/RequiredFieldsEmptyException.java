package org.ssu.belous.exception;

public class RequiredFieldsEmptyException extends RuntimeException {
    public RequiredFieldsEmptyException(String message) {
        super(message);
    }
}
