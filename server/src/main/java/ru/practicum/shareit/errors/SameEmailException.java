package ru.practicum.shareit.errors;

public class SameEmailException extends RuntimeException {
    public SameEmailException(String message) {
        super(message);
    }
}
