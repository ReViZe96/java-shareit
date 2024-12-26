package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.AvailableItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;


    @Override
    public List<ItemRequestResponseDto> getAllAnotherUserRequests(Long requestedUserId) {
        log.info("Пользователь с id = {} пытается посмотреть запросы на создание новой вещи всех пользователей", requestedUserId);
        List<ItemRequest> allAnotherUserRequests = itemRequestRepository.findAll()
                .stream()
                .filter(r -> !requestedUserId.equals(r.getRequestedUserId()))
                .sorted()
                .toList();
        return allAnotherUserRequests.stream().map(this::makeResponseDto).toList();
    }

    @Override
    public List<ItemRequestResponseDto> getOnlyThisUserRequests(Long requestedUserId) {
        log.info("Пользователь с id = {} пытается посмотреть все свои запросы на создание новой вещи", requestedUserId);
        List<ItemRequest> thisUserRequests = itemRequestRepository.findByRequestedUserId(requestedUserId)
                .stream()
                .sorted()
                .toList();
        return thisUserRequests.stream().map(this::makeResponseDto).toList();
    }

    @Override
    public ItemRequestResponseDto getRequestById(Long requestId) {
        log.info("Попытка просмотра информации о запросе с id = {} на создание новой вещи", requestId);
        return makeResponseDto(itemRequestRepository.findById(requestId).get());
    }

    @Override
    public ItemRequestResponseDto addRequest(ItemRequestDto itemRequest, Long requestedUserId) {
        log.info("Бронирующий пользователь c id = {} пытается создать запрос на добавление вещи со следующим описанием: {}", requestedUserId,
                itemRequest.getDescription());
        ItemRequest request = itemRequestMapper.itemRequestDtoToItemRequest(itemRequest, requestedUserId, null);
        return Optional.of(itemRequestRepository.save(request))
                .map(i -> itemRequestMapper.itemRequestToItemRequestResponseDto(i, null))
                .get();
    }

    @Override
    public void deleteAllRequests() {
        itemRequestRepository.deleteAll();
    }


    private ItemRequestResponseDto makeResponseDto(ItemRequest itemRequest) {
        Item availableItem = itemRequest.getRequestedItems();
        AvailableItemDto availableItemDto = availableItem != null ? itemMapper.itemToAvailableItemDto(availableItem) : null;
        return itemRequestMapper.itemRequestToItemRequestResponseDto(itemRequest, availableItemDto);
    }

}
