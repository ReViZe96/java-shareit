package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.AvailableItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestResponseDto {

    private Long id;
    private String description;
    private Boolean created;
    private LocalDateTime creationDate;
    private List<AvailableItemDto> items;

}
