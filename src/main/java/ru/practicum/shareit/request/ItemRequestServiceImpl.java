package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.AvailableItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    //Запросы сортируются по дате создания от более новых к более старым.
    @Override
    public List<ItemRequestResponseDto> getAllAnotherUserRequests(Long requestedUserId) {
        log.info("Пользователь с id = {} пытается посмотреть запросы на бронирование всех пользователей", requestedUserId);
        List<ItemRequest> allAnotherUserRequests = itemRequestRepository.findAll()
                .stream()
                .filter(r -> !requestedUserId.equals(r.getRequestedUserId()))
                .sorted()
                .toList();
        return makeResponseDto(allAnotherUserRequests);
    }

    //Запросы сортируются по дате создания от более новых к более старым.
    @Override
    public List<ItemRequestResponseDto> getOnlyThisUserRequests(Long requestedUserId) {
        log.info("Пользователь с id = {} пытается посмотреть все свои запросы на бронирование", requestedUserId);
        List<ItemRequest> thisUserRequests = itemRequestRepository.findByRequestedUserId(requestedUserId)
                .stream()
                .sorted()
                .toList();
        return makeResponseDto(thisUserRequests);
    }

    @Override
    public ItemRequestResponseDto getRequestById(Long requestId) {
        log.info("Попытка просмотра информации о запросе на бронирование с id = {}", requestId);
        return makeResponseDto(itemRequestRepository.findById(requestId).stream().toList()).get(0);
    }

    @Override
    public ItemRequestResponseDto addRequest(ItemRequestDto itemRequest, Long requestedUserId) {
        log.info("Пользователь c id = {} пытается создать запрос на бронирование вещи со следующим описанием: {}", requestedUserId,
                itemRequest.getDescription());
        List<Item> requestedItems = itemService.findItems(itemRequest.getDescription())
                .stream()
                .map(itemMapper::itemDtoToItem)
                .toList();
        ItemRequest request = itemRequestMapper.itemRequestDtoToItemRequest(itemRequest, requestedUserId, requestedItems);
        List<AvailableItemDto> availableItems = requestedItems.stream().map(itemMapper::itemToAvailableItemDto).toList();
        return Optional.of(itemRequestRepository.save(request))
                .map(i -> itemRequestMapper.itemRequestToItemRequestResponseDto(i, availableItems))
                .get();
    }


    private List<ItemRequestResponseDto> makeResponseDto(List<ItemRequest> itemRequests) {
        List<ItemRequestResponseDto> result = new ArrayList<>();
        for (ItemRequest request : itemRequests) {
            List<AvailableItemDto> availableItems = request.getRequestedItems()
                    .stream()
                    .map(itemMapper::itemToAvailableItemDto)
                    .toList();
            result.add(itemRequestMapper.itemRequestToItemRequestResponseDto(request, availableItems));
        }
        return result;
    }

}
