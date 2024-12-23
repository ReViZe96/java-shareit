package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum BookingFilter {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<BookingFilter> from(String state) {
        switch (state.toUpperCase()) {
            case "ALL":
                return Optional.of(ALL);
            case "CURRENT":
                return Optional.of(CURRENT);
            case "PAST":
                return Optional.of(PAST);
            case "FUTURE":
                return Optional.of(FUTURE);
            case "WAITING":
                return Optional.of(WAITING);
            case "REJECTED":
                return Optional.of(REJECTED);
            default:
                return Optional.empty();
        }
    }

}
