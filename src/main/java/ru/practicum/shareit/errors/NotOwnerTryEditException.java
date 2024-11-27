package ru.practicum.shareit.errors;

public class NotOwnerTryEditException extends RuntimeException {
    public NotOwnerTryEditException(String message) {
        super(message);
    }
}
