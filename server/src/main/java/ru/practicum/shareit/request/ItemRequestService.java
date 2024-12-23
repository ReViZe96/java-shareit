package ru.practicum.shareit.request;


import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {

    List<ItemRequestResponseDto> getAllAnotherUserRequests(Long requestedUserId);

    List<ItemRequestResponseDto> getOnlyThisUserRequests(Long requestedUserId);

    ItemRequestResponseDto getRequestById(Long requestId);

    ItemRequestResponseDto addRequest(ItemRequestDto itemRequest, Long requestedUserId);

    void deleteAllRequests();
}
