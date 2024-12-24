package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingFilter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.errors.NotFoundException;
import ru.practicum.shareit.errors.ForbidenForUserOperationException;
import ru.practicum.shareit.errors.ParameterNotValidException;
import ru.practicum.shareit.errors.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final BookingService bookingService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;


    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllItems(Long ownerId) {
        log.info("Запрос всех вещей пользователя с id = {}", ownerId);
        Optional<User> owner = userRepository.findById(ownerId);
        if (owner.isEmpty()) {
            return Collections.emptyList();
        } else {
            return itemRepository.findByOwner(owner.get()).stream()
                    .map(i -> itemMapper.itemToItemDto(i, bookingService.findLastItemBooking(i),
                        bookingService.findNextItemBooking(i),
                        commentRepository.findByCommentedItem(i).stream().map(commentMapper::commentToCommentDto).toList(),
                        false))
                    .toList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(Long itemId) {
        log.info("Запрос информации о вещи с id = {}", itemId);
        return itemRepository.findById(itemId)
                .map(i -> itemMapper.itemToItemDto(i, bookingService.findLastItemBooking(i),
                    bookingService.findNextItemBooking(i),
                    commentRepository.findByCommentedItem(i).stream().map(commentMapper::commentToCommentDto).toList(),
                    false))
                .get();
    }

    @Override
    @Transactional
    public ItemDto addItem(ItemDto newItem, Long ownerId) {
        log.info("Получен запрос на добавление вещи под названием {}", newItem.getName());
        if (newItem.getRequestId() != null && requestRepository.findById(newItem.getRequestId()).isEmpty()) {
            throw new ValidationException("Запроса с id = " + newItem.getRequestId() + " на создание новой вещи не существует");
        }
        checkItemName(newItem);
        checkItemDescription(newItem);
        checkItemAvailable(newItem);
        isOwnerExist(ownerId);
        log.info("Добавляемая вещь валидна");
        User owner = userRepository.findById(ownerId).get();
        Item item = itemMapper.itemDtoToItem(newItem);
        item.setOwner(owner);
        Item createdItem = null;
        if (newItem.getRequestId() != null) {
            Long createdItemId = itemRepository.save(item).getId();
            createdItem = itemRepository.findById(createdItemId).get();
            ItemRequest request = requestRepository.findById(newItem.getRequestId()).get();
            request.setRequestedItems(createdItem);
            requestRepository.save(request);
        } else {
            createdItem = itemRepository.save(item);
        }
        return Optional.of(createdItem)
                .map(i -> itemMapper.itemToItemDto(i, bookingService.findLastItemBooking(i),
                    bookingService.findNextItemBooking(i),
                    commentRepository.findByCommentedItem(i).stream().map(commentMapper::commentToCommentDto).toList(),
                    false))
                .get();
    }

    @Override
    @Transactional
    public ItemDto editItem(Long itemId, ItemDto editedItem, Long ownerId) {
        log.info("Получен запрос на редактирование информации о вещи с id = {}", itemId);
        User owner = isOwnerExist(ownerId).get();
        log.info("Владелец вещи существует в системе");
        Optional<Item> existItem = itemRepository.findById(itemId);
        if (existItem.isPresent()) {
            log.info("Обновляемая вещь существует в системе");
            if (existItem.get().getOwner().equals(owner)) {
                Item updatingItem = existItem.get();
                if (editedItem.getName() != null) {
                    checkItemName(editedItem);
                    log.info("Новое значение поля name валидно");
                    updatingItem.setName(editedItem.getName());
                }
                if (editedItem.getDescription() != null) {
                    checkItemDescription(editedItem);
                    log.info("Новое значение поля description валидно");
                    updatingItem.setDescription(editedItem.getDescription());
                }
                if (editedItem.getAvailable() != null) {
                    checkItemAvailable(editedItem);
                    log.info("Новое значение поля available валидно");
                    updatingItem.setAvailable(editedItem.getAvailable());
                }
                Item item = itemRepository.save(updatingItem);
                return itemRepository.findById(item.getId())
                        .map(i -> itemMapper.itemToItemDto(i, bookingService.findLastItemBooking(i),
                            bookingService.findNextItemBooking(i),
                            commentRepository.findByCommentedItem(i).stream().map(commentMapper::commentToCommentDto).toList(),
                            false))
                        .get();
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

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> findItems(String text) {
        if (text.isEmpty()) {
            throw new ParameterNotValidException("Поисковой запрос содержит пустую строку");
        } else {
            log.info("Получен поисковой запрос: {}", text);
            Set<Item> withContainedName = new HashSet<>(itemRepository.findAllByNameContainingIgnoreCase(text));
            Set<Item> withContainedDescription = new HashSet<>(itemRepository.findAllByDescriptionContainingIgnoreCase(text));
            withContainedName.addAll(withContainedDescription);
            return withContainedName.stream()
                    .filter(Item::getAvailable)
                    .map(i -> itemMapper.itemToItemDto(i, bookingService.findLastItemBooking(i),
                            bookingService.findNextItemBooking(i),
                            commentRepository.findByCommentedItem(i).stream().map(commentMapper::commentToCommentDto).toList(),
                            false))
                    .toList();
        }
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, CommentDto newComment, Long authorId) {
        log.info("Получен запрос на добавление отзыва на вещь с id = {} пользователя с id = {}", itemId, authorId);
        Map<Item, User> validEntities = isUserCanAddComment(itemId, authorId);
        Item commentedItem = validEntities.keySet().iterator().next();
        User author = validEntities.values().iterator().next();
        Comment comment = commentMapper.commentDtoToComment(newComment, commentedItem, author);
        comment.setCommentedItem(commentedItem);
        comment.setAuthorName(author);
        return Optional.of(commentRepository.save(comment)).map(commentMapper::commentToCommentDto).get();
    }

    @Override
    @Transactional
    public void deleteAllItems() {
        itemRepository.deleteAll();
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
        Optional<User> owner = userRepository.findById(ownerId);
        if (owner.isEmpty()) {
            throw new NotFoundException("Пользователь c id = " + ownerId + ", указанный в качестве владельца не найден");
        } else {
            return owner;
        }
    }

    private Map<Item, User> isUserCanAddComment(Long itemId, Long authorId) {
        Optional<User> author = userRepository.findById(authorId);
        if (author.isEmpty()) {
            throw new NotFoundException("Пользователь c id = " + authorId + " не найден");
        }
        Optional<Item> existItem = itemRepository.findById(itemId);
        if (existItem.isEmpty()) {
            throw new NotFoundException("Вещь с id = " + itemId + " не найдена");
        }
        if (authorId.equals(existItem.get().getOwner().getId())) {
            throw new ValidationException("Владелец вещи не может оставлять отзыв о своей вещи");
        }
        LocalDateTime now = LocalDateTime.now();
        List<Booking> itemBookingsByAuthor = bookingService.getItemAllBookings(existItem.get(), BookingFilter.ALL)
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

}
