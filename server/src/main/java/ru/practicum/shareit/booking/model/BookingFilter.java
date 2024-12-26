package ru.practicum.shareit.booking.model;

public enum BookingFilter {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingFilter from(String state) {
        switch (state.toUpperCase()) {
            case "ALL":
                return ALL;
            case "CURRENT":
                return CURRENT;
            case "PAST":
                return PAST;
            case "FUTURE":
                return FUTURE;
            case "WAITING":
                return WAITING;
            case "REJECTED":
                return REJECTED;
            default:
                return null;
        }
    }

}
