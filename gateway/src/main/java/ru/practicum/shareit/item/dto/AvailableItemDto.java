package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class AvailableItemDto {

    private Long id;
    private String name;
    private Long ownerId;

}
