package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingFilter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.errors.NotFoundException;
import ru.practicum.shareit.errors.ForbidenForUserOperationException;
import ru.practicum.shareit.errors.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserStorage;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;


    public List<ItemDto> getAllItems(Long ownerId) {
        log.info("Запрос всех вещей пользователя с id = {}", ownerId);
        return itemStorage.getAllItems(ownerId).stream().map(i -> itemMapper.itemToItemDto(i, findLastItemBooking(i),
                findNextItemBooking(i), findAllItemComments(i), false)).toList();
    }

    public ItemDto getItemById(Long itemId) {
        log.info("Запрос информации о вещи с id = {}", itemId);
        return itemStorage.getItemById(itemId).map(i -> itemMapper.itemToItemDto(i, findLastItemBooking(i),
                findNextItemBooking(i), findAllItemComments(i), false)).get();
    }

    public ItemDto addItem(ItemDto newItem, Long ownerId) {
        log.info("Получен запрос на добавление вещи под названием {}", newItem.getName());
        checkItemName(newItem);
        checkItemDescription(newItem);
        checkItemAvailable(newItem);
        isOwnerExist(ownerId);
        log.info("Добавляемая вещь валидна");
        User owner = userStorage.getUserById(ownerId).get();
        Item item = itemMapper.itemDtoToItem(newItem);
        item.setOwner(owner);
        return itemStorage.addItem(item).map(i -> itemMapper.itemToItemDto(i, findLastItemBooking(i), findNextItemBooking(i),
                findAllItemComments(i), false)).get();
    }

    public ItemDto editItem(Long itemId, ItemDto editedItem, Long ownerId) {
        log.info("Получен запрос на редактирование информации о вещи с id = {}", itemId);
        Map<String, Object> updatedField = new HashMap<>();
        if (editedItem.getName() != null) {
            checkItemName(editedItem);
            log.info("Новое значение поля name валидно");
            updatedField.put(ItemStorage.NAME, editedItem.getName());
        }
        if (editedItem.getDescription() != null) {
            checkItemDescription(editedItem);
            log.info("Новое значение поля description валидно");
            updatedField.put(ItemStorage.DESCRIPTION, editedItem.getDescription());
        }
        if (editedItem.getAvailable() != null) {
            checkItemAvailable(editedItem);
            log.info("Новое значение поля available валидно");
            updatedField.put(ItemStorage.AVAILABLE, editedItem.getAvailable());
        }
        User owner = isOwnerExist(ownerId).get();
        log.info("Владелец вещи существует в системе");
        Optional<Item> existItem = itemStorage.getItemById(itemId);
        if (existItem.isPresent()) {
            log.info("Обновляемая вещь существует в системе");
            if (existItem.get().getOwner().equals(owner)) {
                itemStorage.editItem(itemId, updatedField, owner);
                return itemStorage.getItemById(itemId).map(i -> itemMapper.itemToItemDto(i, findLastItemBooking(i),
                        findNextItemBooking(i), findAllItemComments(i), false)).get();
            } else {
                throw new ForbidenForUserOperationException("Пользователю " + owner.getName() +
                        " запрещено редактировать информацию о вещи " +
                        existItem.get().getName() + " т.к. владельцем вещи является другой пользователь: " +
                        existItem.get().getOwner().getName());
            }
        } else {
            throw new NotFoundException("Обновляемая вещь с id = " + itemId + " не найдена в системе");
        }
    }

    public List<ItemDto> findItems(String text) {
        if (text.isEmpty()) {
            log.info("Поисковой запрос содержит пустую строку");
            return Collections.emptyList();
        } else {
            log.info("Получен поисковой запрос: {}", text);
            return itemStorage.findItems(text).stream()
                    .filter(Item::getAvailable)
                    .map(i -> itemMapper.itemToItemDto(i, findLastItemBooking(i), findNextItemBooking(i),
                            findAllItemComments(i), false))
                    .toList();
        }
    }

    public CommentDto addComment(Long itemId, CommentDto newComment, Long authorId) {
        log.info("Получен запрос на добавление отзыва на вещь с id = {} пользователя с id = {}", itemId, authorId);
        Map<Item, User> validEntities = isUserCanAddComment(itemId, authorId);
        Item commentedItem = validEntities.keySet().iterator().next();
        User author = validEntities.values().iterator().next();
        Comment comment = commentMapper.commentDtoToComment(newComment, commentedItem, author);
        comment.setCommentedItem(commentedItem);
        comment.setAuthorName(author);
        return itemStorage.addComment(comment).map(commentMapper::commentToCommentDto).get();
    }


    private void checkItemName(ItemDto item) {
        String name = item.getName();
        if (name == null || name.isEmpty() || name.isBlank()) {
            throw new ValidationException("У вещи должно быть название");
        }
    }

    private void checkItemDescription(ItemDto item) {
        String description = item.getDescription();
        if (description == null || description.isEmpty() || description.isBlank()) {
            throw new ValidationException("У вещи должно быть описание");
        }
    }

    private void checkItemAvailable(ItemDto item) {
        Boolean available = item.getAvailable();
        if (available == null) {
            throw new ValidationException("У вещи должен быть указан статус её доступности для бронирования");
        }
    }

    private Optional<User> isOwnerExist(Long ownerId) {
        Optional<User> owner = userStorage.getUserById(ownerId);
        if (owner.isEmpty()) {
            throw new NotFoundException("Пользователь c id = " + ownerId + ", указанный в качестве владельца не найден");
        } else {
            return owner;
        }
    }

    private Map<Item, User> isUserCanAddComment(Long itemId, Long authorId) {
        Optional<User> author = userStorage.getUserById(authorId);
        if (author.isEmpty()) {
            throw new NotFoundException("Пользователь c id = " + authorId + " не найден");
        }
        Optional<Item> existItem = itemStorage.getItemById(itemId);
        if (existItem.isEmpty()) {
            throw new NotFoundException("Вещь с id = " + itemId + " не найдена");
        }
        if (authorId.equals(existItem.get().getOwner().getId())) {
            throw new ValidationException("Владелец вещи не может оставлять отзыв о своей вещи");
        }
        LocalDateTime now = LocalDateTime.now();
        List<Booking> itemBookingsByAuthor = bookingStorage.getAllItemBookings(existItem.get(), BookingFilter.ALL)
                .stream()
                .filter(b -> authorId.equals(b.getRequestedUser().getId()))
                .filter(b -> BookingStatus.APPROVED.equals(b.getStatus()))
                .filter(b -> now.isAfter(b.getEnd()))
                .toList();
        if (itemBookingsByAuthor.isEmpty()) {
            throw new ValidationException("Пользователь " + author.get().getName() + " не имеет права " +
                    "оставлять отзыв на вещь " + existItem.get().getName() + " т.к. никогда не брал её в аренду, " +
                    "либо срок аренды еще не истёк");
        }
        log.info("Пользователь {} имеет право оставить отзыв на вещь {}", author.get().getName(), existItem.get().getName());
        HashMap<Item, User> validEntities = new HashMap<>();
        validEntities.put(existItem.get(), author.get());
        return validEntities;
    }

    public Booking findLastItemBooking(Item item) {
        List<Booking> itemBookings = bookingRepository.findByRequestedItem(item);
        LocalDateTime now = LocalDateTime.now();
        return itemBookings.stream()
                .filter(b -> now.isAfter(b.getStart()))
                .min(Collections.reverseOrder())
                .orElse(null);
    }

    public Booking findNextItemBooking(Item item) {
        List<Booking> itemBookings = bookingRepository.findByRequestedItem(item);
        LocalDateTime now = LocalDateTime.now();
        return itemBookings.stream()
                .filter(b -> now.isBefore(b.getStart()))
                .sorted()
                .findFirst()
                .orElse(null);
    }

    public List<CommentDto> findAllItemComments(Item item) {
        return commentRepository.findByCommentedItem(item)
                .stream()
                .map(commentMapper::commentToCommentDto)
                .toList();
    }

}
