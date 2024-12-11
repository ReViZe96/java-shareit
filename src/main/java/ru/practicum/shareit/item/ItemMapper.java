package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final CommentMapper commentMapper;

    public Item itemDtoToItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public ItemDto itemToItemDto(Item item, boolean isOwner) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        List<Booking> allBooking = bookingRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        Optional<Booking> lastBookings = allBooking.stream()
                .filter(b -> now.isAfter(b.getStart()))
                .min(Collections.reverseOrder());
        itemDto.setLastBooking(isOwner ? lastBookings.orElse(null) : null);
        Optional<Booking> nextBookings = allBooking.stream()
                .filter(b -> now.isBefore(b.getStart()))
                .sorted()
                .findFirst();
        itemDto.setNextBooking(isOwner ? nextBookings.orElse(null) : null);
        itemDto.setComments(commentRepository.findByCommentedItem(item)
                .stream()
                .map(commentMapper::commentToCommentDto)
                .toList());
        return itemDto;
    }

}
