package ru.csi.exception;

public class BadEntryDataException extends RuntimeException {
    public BadEntryDataException(String message) {
        super(message);
    }
}
