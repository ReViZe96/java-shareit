package ru.practicum.shareit.integration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.errors.ForbidenForUserOperationException;
import ru.practicum.shareit.errors.ParameterNotValidException;
import ru.practicum.shareit.errors.ValidationException;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
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
public class ItemServiceTest {

    private final EntityManager em;
    private final ItemServiceImpl itemService;
    private final UserServiceImpl userService;
    private final BookingServiceImpl bookingService;
    private final ItemRequestService requestService;

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

    private ItemDto makeItemWithRequestIdDto(String name, String description, Boolean available, Long requestId) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);
        itemDto.setRequestId(requestId);
        return itemDto;
    }


    @BeforeEach
    public void addOwnerAndFirstItem() {
        UserDto ownerDto = makeUserDto("Owner", "owner@email.com");
        Long ownerId = userService.addUser(ownerDto).getId();
        ItemDto itemDto = makeItemDto("FirstItem", "first item description", true);
        itemService.addItem(itemDto, ownerId);
    }

    @AfterEach
    public void clear() {
        itemService.deleteAllItems();
        userService.deleteAllUsers();
    }


    @Test
    public void shouldGetAllItems() {
        ItemDto secondItemDto = makeItemDto("SecondItem", "second item description", false);
        ItemDto thirdItemDto = makeItemDto("ThirdItem", "third item description", true);
        Long ownerId = getOwnerId();
        itemService.addItem(secondItemDto, ownerId);
        itemService.addItem(thirdItemDto, ownerId);

        List<ItemDto> allItems = itemService.getAllItems(ownerId);
        assertThat(allItems.size(), equalTo(3));
    }

    @Test
    public void shouldGetItemById() {
        ItemDto savedItemDto = makeItemDto("SavedItem", "saved item description", false);
        Long ownerId = getOwnerId();
        Long savedItemId = itemService.addItem(savedItemDto, ownerId).getId();

        ItemDto item = itemService.getItemById(savedItemId);
        assertThat(savedItemId, equalTo(item.getId()));
        assertThat(savedItemDto.getName(), equalTo(item.getName()));
        assertThat(savedItemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(savedItemDto.getAvailable(), equalTo(item.getAvailable()));
    }

    @Test
    public void shouldAddItem() {
        ItemDto addedItemDto = makeItemDto("AddedItem", "added item description", true);
        Long ownerId = getOwnerId();

        Long addedItemId = itemService.addItem(addedItemDto, ownerId).getId();
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name and i.description = :description",
                Item.class);
        Item item = query
                .setParameter("name", addedItemDto.getName())
                .setParameter("description", addedItemDto.getDescription())
                .getSingleResult();
        assertThat(addedItemId, equalTo(item.getId()));
        assertThat(addedItemDto.getName(), equalTo(item.getName()));
        assertThat(addedItemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(addedItemDto.getAvailable(), equalTo(item.getAvailable()));

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("first item request");
        Long requestedUser = makeUserDto("RequestedUser", "requestedUser@email.com").getId();
        Long requestId = requestService.addRequest(requestDto, requestedUser).getId();
        ItemDto itemAddedOnRequest = makeItemWithRequestIdDto("Item on Request", "with requestId",
                true, requestId);
        ItemDto itemOnRequest = itemService.addItem(itemAddedOnRequest, ownerId);
        assertThat(itemAddedOnRequest.getName(), equalTo(itemOnRequest.getName()));
        assertThat(itemAddedOnRequest.getDescription(), equalTo(itemOnRequest.getDescription()));

        ItemDto itemOnNotExistRequest = makeItemWithRequestIdDto("Item on not exist request",
                "this request not exist", true, 1000L);
        Assertions.assertThrowsExactly(ValidationException.class, () -> itemService.addItem(itemOnNotExistRequest, ownerId));
    }

    @Test
    public void shouldEditItem() {
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name and i.description = :description",
                Item.class);
        Item firstItem = query
                .setParameter("name", "FirstItem")
                .setParameter("description", "first item description")
                .getSingleResult();
        Long updatedItemId = firstItem.getId();
        Long ownerId = firstItem.getOwner().getId();
        ItemDto updatedItemDto = makeItemDto("EditedItem", "edited item description", true);

        ItemDto item = itemService.editItem(updatedItemId, updatedItemDto, ownerId);
        assertThat(updatedItemId, equalTo(item.getId()));
        assertThat(updatedItemDto.getName(), equalTo(item.getName()));
        assertThat(updatedItemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(updatedItemDto.getAvailable(), equalTo(item.getAvailable()));

        UserDto notOwner = makeUserDto("NotOwner", "notOwner@email.com");
        Long notOwnerId = userService.addUser(notOwner).getId();

        Assertions.assertThrowsExactly(ForbidenForUserOperationException.class, () -> itemService.editItem(updatedItemId,
                updatedItemDto, notOwnerId));
    }

    @Test
    public void shouldFindItems() {
        ItemDto foundedFirstItemDto = makeItemDto("FoundedItem", "1 item description", true);
        ItemDto foundedSecondItemDto = makeItemDto("AnotherItem", "founded item description", true);
        Long ownerId = getOwnerId();
        ItemDto firstFounded = itemService.addItem(foundedFirstItemDto, ownerId);
        ItemDto secondFounded = itemService.addItem(foundedSecondItemDto, ownerId);

        List<ItemDto> foundedItems = itemService.findItems("founded");
        assertThat(foundedItems.size(), equalTo(2));
        Assertions.assertTrue(foundedItems.contains(firstFounded));
        Assertions.assertTrue(foundedItems.contains(secondFounded));

        Assertions.assertThrowsExactly(ParameterNotValidException.class, () -> itemService.findItems(""));
    }

    @Test
    public void shouldAddComment() throws InterruptedException {
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name and i.description = :description",
                Item.class);
        Item firstItem = query
                .setParameter("name", "FirstItem")
                .setParameter("description", "first item description")
                .getSingleResult();
        Long commentedItemId = firstItem.getId();
        UserDto authorDto = makeUserDto("Author", "author@email.com");
        Long authorId = userService.addUser(authorDto).getId();

        BookingRequestDto bookingRequest = new BookingRequestDto();
        bookingRequest.setItemId(commentedItemId);
        bookingRequest.setStart(LocalDateTime.now().plusSeconds(1));
        bookingRequest.setEnd(LocalDateTime.now().plusSeconds(1));
        Long bookingId = bookingService.addBooking(authorId, bookingRequest).getId();
        bookingService.approveBooking(getOwnerId(), bookingId, true);
        Thread.sleep(1000);

        CommentDto newComment = new CommentDto();
        newComment.setText("first comment");
        LocalDateTime beforeCreating = LocalDateTime.now();

        CommentDto comment = itemService.addComment(commentedItemId, newComment, authorId);
        assertThat(comment.getId(), notNullValue());
        assertThat(newComment.getText(), equalTo(comment.getText()));
        assertThat(userService.getUserById(authorId).getName(), equalTo(comment.getAuthorName()));
        assertThat(firstItem.getName(), equalTo(comment.getCommentedItem()));
        Assertions.assertTrue(beforeCreating.isBefore(comment.getCreated()));
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

}
