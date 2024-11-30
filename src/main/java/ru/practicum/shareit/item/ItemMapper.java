package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.model.Item;

@Mapper
public interface ItemMapper {

    Item itemDtoToItem(ItemDto itemDto);

    ItemDto itemToItemDto(Item item);

}
