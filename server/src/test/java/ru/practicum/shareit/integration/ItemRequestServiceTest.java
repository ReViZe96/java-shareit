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
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {

    private final EntityManager em;
    private final UserServiceImpl userService;
    private final ItemRequestServiceImpl requestService;

    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

    private ItemRequestDto makeRequestDto(String description) {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription(description);
        return requestDto;
    }

    @BeforeEach
    public void initData() {
        UserDto firstUserDto = makeUserDto("FirstUser", "firstUser@email.com");
        Long firstUserId = userService.addUser(firstUserDto).getId();
        ItemRequestDto firstRequestDto = makeRequestDto("First item request");
        requestService.addRequest(firstRequestDto, firstUserId);

        UserDto secondUserDto = makeUserDto("SecondUser", "secondUser@email.com");
        Long secondUserId = userService.addUser(secondUserDto).getId();
        ItemRequestDto secondRequestDto = makeRequestDto("Second item request");
        requestService.addRequest(secondRequestDto, secondUserId);
    }

    @AfterEach
    public void clear() {
        requestService.deleteAllRequests();
        userService.deleteAllUsers();
    }

    @Test
    public void shouldGetAllAnotherUserRequests() {
        Long firstuserId = getFirstUserId();

        List<ItemRequestResponseDto> anotherUserRequests = requestService.getAllAnotherUserRequests(firstuserId);
        assertThat(anotherUserRequests.size(), equalTo(1));
        assertThat(anotherUserRequests.get(0).getId(), notNullValue());
        assertThat(anotherUserRequests.get(0).getDescription(), equalTo("Second item request"));
        assertThat(anotherUserRequests.get(0).getCreated(), equalTo(true));
        assertThat(anotherUserRequests.get(0).getCreationDate(), notNullValue());
    }

    @Test
    public void shouldGetOnlyThisUserRequests() {
        Long firstuserId = getFirstUserId();

        List<ItemRequestResponseDto> anotherUserRequests = requestService.getOnlyThisUserRequests(firstuserId);
        assertThat(anotherUserRequests.size(), equalTo(1));
        assertThat(anotherUserRequests.get(0).getId(), notNullValue());
        assertThat(anotherUserRequests.get(0).getDescription(), equalTo("First item request"));
        assertThat(anotherUserRequests.get(0).getCreated(), equalTo(true));
        assertThat(anotherUserRequests.get(0).getCreationDate(), notNullValue());
    }

    @Test
    public void shouldGetRequestById() {
        TypedQuery<ItemRequest> secondRequestQuery = em.createQuery("Select r from ItemRequest r where description =:description",
                ItemRequest.class);
        ItemRequest secondItemRequest = secondRequestQuery
                .setParameter("description", "Second item request")
                .getSingleResult();
        Long secondRequestId = secondItemRequest.getId();

        ItemRequestResponseDto responseDto = requestService.getRequestById(secondRequestId);
        assertThat(secondRequestId, equalTo(responseDto.getId()));
        assertThat("Second item request", equalTo(responseDto.getDescription()));
    }

    @Test
    public void shouldAddRequest() {
        Long firstUserId = getFirstUserId();
        List<ItemRequestResponseDto> firstUserRequestsBefore = requestService.getOnlyThisUserRequests(firstUserId);

        assertThat(firstUserRequestsBefore.size(), equalTo(1));
        ItemRequestDto thirdRequestDto = makeRequestDto("Third item request");

        ItemRequestResponseDto thirdRequest = requestService.addRequest(thirdRequestDto, firstUserId);
        List<ItemRequestResponseDto> firstUserRequestsAfter = requestService.getOnlyThisUserRequests(firstUserId);
        assertThat(firstUserRequestsAfter.size(), equalTo(2));
        Assertions.assertTrue(firstUserRequestsAfter.contains(thirdRequest));
    }


    private Long getFirstUserId() {
        TypedQuery<User> userQuery = em.createQuery("Select u from User u where u.name = :name and u.email = :email",
                User.class);
        User firstuser = userQuery
                .setParameter("name", "FirstUser")
                .setParameter("email", "firstUser@email.com")
                .getSingleResult();
        return firstuser.getId();
    }

}
