package ru.practicum.shareit.errors;

public class ForbidenForUserOperationException extends RuntimeException {
    public ForbidenForUserOperationException(String message) {
        super(message);
    }
}
