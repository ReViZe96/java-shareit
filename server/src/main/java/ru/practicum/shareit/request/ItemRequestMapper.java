package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.AvailableItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ItemRequestMapper {

    public ItemRequest itemRequestDtoToItemRequest(ItemRequestDto requestDto, Long requestedUserId,
                                                   Item requestedItems) {
        ItemRequest request = new ItemRequest();
        request.setId(null);
        request.setDescription(requestDto.getDescription());
        request.setCreated(false);
        request.setRequestedUserId(requestedUserId);
        request.setCreationDate(LocalDateTime.now());
        request.setRequestedItems(requestedItems);
        return request;
    }

    public ItemRequestResponseDto itemRequestToItemRequestResponseDto(ItemRequest request, AvailableItemDto availableItem) {
        ItemRequestResponseDto responseDto = new ItemRequestResponseDto();
        responseDto.setId(request.getId());
        responseDto.setDescription(request.getDescription());
        responseDto.setCreated(true);
        responseDto.setCreationDate(request.getCreationDate());
        responseDto.setItems(availableItem != null ? List.of(availableItem) : null);
        return responseDto;
    }

}
