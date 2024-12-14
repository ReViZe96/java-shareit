package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<ItemDto> getAllItems(Long ownerId);

    ItemDto getItemById(Long itemId);

    ItemDto addItem(ItemDto newItem, Long ownerId);

    ItemDto editItem(Long itemId, ItemDto editedItem, Long ownerId);

    List<ItemDto> findItems(String text);

    CommentDto addComment(Long itemId, CommentDto newComment, Long authorId);

    /**
     * Вернуть последнее бронирование вещи относительно текущей даты
     *
     * @param item вещь, для которой осуществляется поиск последнего бронирования
     */
    Booking findLastItemBooking(Item item);

    /**
     * Вернуть ближайшее следующее бронирование вещи относительно текущей даты
     *
     * @param item вещь, для которой осуществляется поиск ближайшего следующего бронирования
     */
    Booking findNextItemBooking(Item item);

    /**
     * Вернуть список отзывов на конкретную вешь
     *
     * @param item вещь, список отзывов на которую необходимо вернуть
     */
    List<CommentDto> findAllItemComments(Item item);

}
