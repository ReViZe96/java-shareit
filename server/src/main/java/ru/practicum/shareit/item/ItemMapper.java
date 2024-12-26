package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.AvailableItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Component
public class ItemMapper {

    public Item itemDtoToItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public ItemDto itemToItemDto(Item item, Booking lastBooking, Booking nextBooking,
                                 List<CommentDto> comments, boolean isOwner) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setLastBooking(isOwner ? lastBooking.getId() : null);
        itemDto.setNextBooking(isOwner ? nextBooking.getId() : null);
        itemDto.setComments(comments);
        return itemDto;
    }

    public AvailableItemDto itemToAvailableItemDto(Item item) {
        AvailableItemDto availableItemDto = new AvailableItemDto();
        availableItemDto.setId(item.getId());
        availableItemDto.setName(item.getName());
        availableItemDto.setOwnerId(item.getOwner().getId());
        return availableItemDto;
    }

}
