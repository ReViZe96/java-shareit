package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequestDto {

    Long itemId;
    LocalDateTime start;
    LocalDateTime end;

}
