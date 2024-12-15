package ru.practicum.shareit.errors;

public class ParameterNotValidException extends RuntimeException {
    public ParameterNotValidException(String message) {
        super(message);
    }
}
