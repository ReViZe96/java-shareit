package ru.practicum.shareit.integration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingFilter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {

    private final EntityManager em;
    private final ItemServiceImpl itemService;
    private final UserServiceImpl userService;
    private final BookingServiceImpl bookingService;
    private final ItemMapper itemMapper;

    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        return dto;
    }

    private BookingRequestDto makeBookingRequestDto(LocalDateTime start, LocalDateTime end, Long itemId) {
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setStart(start);
        bookingRequestDto.setEnd(end);
        bookingRequestDto.setItemId(itemId);
        return bookingRequestDto;
    }

    @BeforeEach
    public void initData() {
        UserDto ownerDto = makeUserDto("Owner", "owner@email.com");
        Long ownerId = userService.addUser(ownerDto).getId();
        ItemDto itemDto = makeItemDto("TestingItem", "testing item description", true);
        Long itemId = itemService.addItem(itemDto, ownerId).getId();

        UserDto requestedUser = makeUserDto("RequestedUser", "requestedUser@email.com");
        Long requestedUserId = userService.addUser(requestedUser).getId();
        BookingRequestDto firstBookingRequestDto = makeBookingRequestDto(
                LocalDateTime.of(2025, 12, 21, 10, 0),
                LocalDateTime.of(2025, 12, 22, 9, 0),
                itemId);
        bookingService.addBooking(requestedUserId, firstBookingRequestDto);
        BookingRequestDto secondBookingRequestDto = makeBookingRequestDto(
                LocalDateTime.of(2025, 12, 23, 11, 0),
                LocalDateTime.of(2025, 12, 24, 10, 0),
                itemId);
        Long secondBookingId = bookingService.addBooking(requestedUserId, secondBookingRequestDto).getId();
        bookingService.approveBooking(ownerId, secondBookingId, true);
        BookingRequestDto thirdBookingRequestDto = makeBookingRequestDto(
                LocalDateTime.of(2025, 12, 25, 12, 0),
                LocalDateTime.of(2025, 12, 26, 11, 0),
                itemId);
        Long thirdBookingId = bookingService.addBooking(requestedUserId, thirdBookingRequestDto).getId();
        bookingService.approveBooking(ownerId, thirdBookingId, false);
    }

    @AfterEach
    public void clear() {
        itemService.deleteAllItems();
        userService.deleteAllUsers();
    }


    @Test
    public void shouldGetAllUserBookings() {
        Long requestedUserId = getRequestedUserId();
        Long requestedItemId = getRequestedItemId();

        List<BookingResponseDto> allUserBooking = bookingService.getAllUserBookings(requestedUserId, BookingFilter.ALL);
        assertThat(allUserBooking.size(), equalTo(3));
        assertThat(allUserBooking.get(0).getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(allUserBooking.get(0).getBooker(), equalTo(userService.getUserById(requestedUserId)));
        assertThat(allUserBooking.get(0).getItem(), equalTo(itemService.getItemById(requestedItemId)));

        List<BookingResponseDto> futureUserBooking = bookingService.getAllUserBookings(requestedUserId, BookingFilter.FUTURE);
        assertThat(futureUserBooking.size(), equalTo(3));
        assertThat(futureUserBooking.get(0).getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(futureUserBooking.get(0).getBooker(), equalTo(userService.getUserById(requestedUserId)));
        assertThat(futureUserBooking.get(0).getItem(), equalTo(itemService.getItemById(requestedItemId)));

        List<BookingResponseDto> currentUserBooking = bookingService.getAllUserBookings(requestedUserId, BookingFilter.CURRENT);
        assertThat(currentUserBooking.size(), equalTo(0));
    }

    @Test
    public void shouldGetAllItemBookings() throws InterruptedException {
        Long ownerId = getOwnerId();
        Long requestedUserId = getRequestedUserId();
        Long requestedItemId = getRequestedItemId();

        LocalDateTime startBooking = LocalDateTime.now().plusSeconds(1);
        LocalDateTime endBooking = LocalDateTime.now().plusSeconds(1);
        BookingRequestDto lastBookingRequestDto = makeBookingRequestDto(
                startBooking, endBooking, requestedItemId);
        bookingService.addBooking(requestedUserId, lastBookingRequestDto).getId();
        Thread.sleep(1000);


        List<BookingResponseDto> waitingOwnerBooking = bookingService.getAllItemBookings(ownerId, BookingFilter.WAITING);
        assertThat(waitingOwnerBooking.size(), equalTo(2));
        assertThat(waitingOwnerBooking.get(0).getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(waitingOwnerBooking.get(0).getBooker(), equalTo(userService.getUserById(requestedUserId)));
        assertThat(waitingOwnerBooking.get(0).getItem().getId(), equalTo(requestedItemId));

        List<BookingResponseDto> rejectedOwnerBooking = bookingService.getAllItemBookings(ownerId, BookingFilter.REJECTED);
        assertThat(rejectedOwnerBooking.size(), equalTo(1));
        assertThat(rejectedOwnerBooking.get(0).getStatus(), equalTo(BookingStatus.REJECTED));
        assertThat(rejectedOwnerBooking.get(0).getBooker(), equalTo(userService.getUserById(requestedUserId)));
        assertThat(rejectedOwnerBooking.get(0).getItem().getId(), equalTo(requestedItemId));
        assertThat(rejectedOwnerBooking.get(0).getStart(), equalTo(
                LocalDateTime.of(2025, 12, 25, 12, 0)));
        assertThat(rejectedOwnerBooking.get(0).getEnd(), equalTo(
                LocalDateTime.of(2025, 12, 26, 11, 0)));

        List<BookingResponseDto> allOwnerBooking = bookingService.getAllItemBookings(ownerId, BookingFilter.ALL);
        assertThat(allOwnerBooking.size(), equalTo(4));
    }

    @Test
    public void shouldGetBookingById() {
        Long requestedItemId = getRequestedItemId();
        Long requestedUserId = getRequestedUserId();
        BookingRequestDto fourthBookingRequestDto = makeBookingRequestDto(
                LocalDateTime.of(2026, 12, 21, 10, 0),
                LocalDateTime.of(2026, 12, 22, 9, 0),
                requestedItemId);
        Long fourthBookingId = bookingService.addBooking(requestedUserId, fourthBookingRequestDto).getId();

        BookingResponseDto booking = bookingService.getBookingById(requestedUserId, fourthBookingId);
        assertThat(LocalDateTime.of(2026, 12, 21, 10, 0), equalTo(booking.getStart()));
        assertThat(LocalDateTime.of(2026, 12, 22, 9, 0), equalTo(booking.getEnd()));
        assertThat(itemService.getItemById(requestedItemId), equalTo(booking.getItem()));
        assertThat(userService.getUserById(requestedUserId), equalTo(booking.getBooker()));
    }

    @Test
    public void shouldAddBooking() {
        Long requestedItemId = getRequestedItemId();
        Long requestedUserId = getRequestedUserId();
        BookingRequestDto newBookingRequestDto = makeBookingRequestDto(
                LocalDateTime.of(2027, 12, 21, 10, 0),
                LocalDateTime.of(2027, 12, 22, 9, 0),
                requestedItemId);
        Long newBooking = bookingService.addBooking(requestedUserId, newBookingRequestDto).getId();
        BookingResponseDto booking = bookingService.getBookingById(requestedUserId, newBooking);
        assertThat(LocalDateTime.of(2027, 12, 21, 10, 0), equalTo(booking.getStart()));
        assertThat(LocalDateTime.of(2027, 12, 22, 9, 0), equalTo(booking.getEnd()));
        assertThat(itemService.getItemById(requestedItemId), equalTo(booking.getItem()));
        assertThat(userService.getUserById(requestedUserId), equalTo(booking.getBooker()));
    }

    @Test
    public void shouldApproveBooking() {
        Long ownerId = getOwnerId();
        Long requestedItemId = getRequestedItemId();
        Long requestedUserId = getRequestedUserId();
        BookingRequestDto waitingBookingRequestDto = makeBookingRequestDto(
                LocalDateTime.of(2028, 12, 21, 10, 0),
                LocalDateTime.of(2028, 12, 22, 9, 0),
                requestedItemId);
        Long waitingBookingId = bookingService.addBooking(requestedUserId, waitingBookingRequestDto).getId();

        BookingResponseDto firstBookingAfterApprove = bookingService.approveBooking(ownerId, waitingBookingId, true);
        assertThat(firstBookingAfterApprove.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    public void shouldGetItemAllBookings() {
        Item requestedItem = itemMapper.itemDtoToItem(itemService.getItemById(getRequestedItemId()));

        List<Booking> futureItemBookings = bookingService.getItemAllBookings(requestedItem, BookingFilter.REJECTED);
        assertThat(futureItemBookings.size(), equalTo(1));
        assertThat(futureItemBookings.get(0).getStatus(), equalTo(BookingStatus.REJECTED));
        assertThat(futureItemBookings.get(0).getRequestedItem().getId(), equalTo(requestedItem.getId()));
    }

    @Test
    public void shouldFindLastItemBooking() throws InterruptedException {
        Long requestedItemId = getRequestedItemId();
        Long requestedUserId = getRequestedUserId();
        LocalDateTime startBooking = LocalDateTime.now().plusSeconds(1);
        LocalDateTime endBooking = LocalDateTime.now().plusSeconds(1);
        BookingRequestDto lastBookingRequestDto = makeBookingRequestDto(
                startBooking, endBooking, requestedItemId);
        bookingService.addBooking(requestedUserId, lastBookingRequestDto).getId();
        Item requestedItem = itemMapper.itemDtoToItem(itemService.getItemById(getRequestedItemId()));

        Thread.sleep(1000);
        Booking lastItemBooking = bookingService.findLastItemBooking(requestedItem);
        assertThat(requestedItem.getId(), equalTo(lastItemBooking.getRequestedItem().getId()));
        assertThat(startBooking, equalTo(lastItemBooking.getStart()));
        assertThat(endBooking, equalTo(lastItemBooking.getEnd()));
    }

    @Test
    public void shouldFindNextItemBooking() {
        Item requestedItem = itemMapper.itemDtoToItem(itemService.getItemById(getRequestedItemId()));

        Booking nextItemBooking = bookingService.findNextItemBooking(requestedItem);
        assertThat(requestedItem.getId(), equalTo(nextItemBooking.getRequestedItem().getId()));
        assertThat(LocalDateTime.of(2025, 12, 21, 10, 0), equalTo(nextItemBooking.getStart()));
        assertThat(LocalDateTime.of(2025, 12, 22, 9, 0), equalTo(nextItemBooking.getEnd()));
    }


    private Long getRequestedUserId() {
        TypedQuery<User> query = em.createQuery("Select u from User u where u.name = :name and u.email = :email",
                User.class);
        User requestedUser = query
                .setParameter("name", "RequestedUser")
                .setParameter("email", "requestedUser@email.com")
                .getSingleResult();
        return requestedUser.getId();
    }

    private Long getOwnerId() {
        TypedQuery<User> query = em.createQuery("Select u from User u where u.name = :name and u.email = :email",
                User.class);
        User owner = query
                .setParameter("name", "Owner")
                .setParameter("email", "owner@email.com")
                .getSingleResult();
        return owner.getId();
    }

    private Long getRequestedItemId() {
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name and i.description = :description",
                Item.class);
        Item requestedItem = query
                .setParameter("name", "TestingItem")
                .setParameter("description", "testing item description")
                .getSingleResult();
        return requestedItem.getId();
    }

}
