package ru.practicum.shareit.integration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {

    private final EntityManager em;
    private final UserServiceImpl userService;

    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

    @BeforeEach
    public void addFirstUser() {
        UserDto userDto = makeUserDto("FirstUser", "firstUser@email.com");
        userService.addUser(userDto);

    }

    @AfterEach
    public void clear() {
        userService.deleteAllUsers();
    }


    @Test
    public void shouldGetAllUsers() {
        UserDto secondUserDto = makeUserDto("SecondUser", "secondUser@email.com");
        UserDto thirdUserDto = makeUserDto("ThirdUser", "thirdUser@email.com");
        userService.addUser(secondUserDto);
        userService.addUser(thirdUserDto);

        Collection<UserDto> allUsers = userService.getAllUsers();
        Assertions.assertEquals(3, allUsers.size());
    }

    @Test
    public void shouldGetUserById() {
        UserDto savedUserDto = makeUserDto("SavedUser", "savedUser@email.com");
        Long savedUserId = userService.addUser(savedUserDto).getId();

        UserDto user = userService.getUserById(savedUserId);
        assertThat(savedUserId, equalTo(user.getId()));
        assertThat(savedUserDto.getName(), equalTo(user.getName()));
        assertThat(savedUserDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    public void shouldAddUser() {
        UserDto addedUserDto = makeUserDto("AddedUser", "addedUser@email.com");

        Long addedUserId = userService.addUser(addedUserDto).getId();
        TypedQuery<User> query = em.createQuery("Select u from User u where u.name = :name and u.email = :email",
                User.class);
        User user = query
                .setParameter("name", addedUserDto.getName())
                .setParameter("email", addedUserDto.getEmail())
                .getSingleResult();
        assertThat(addedUserId, equalTo(user.getId()));
        assertThat(addedUserDto.getName(), equalTo(user.getName()));
        assertThat(addedUserDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    public void shouldUpdateUser() {
        TypedQuery<User> query = em.createQuery("Select u from User u where u.name = :name and u.email = :email", User.class);
        User firstuser = query
                .setParameter("name", "FirstUser")
                .setParameter("email", "firstUser@email.com")
                .getSingleResult();
        Long updatedUserId = firstuser.getId();
        UserDto updatedUserDto = makeUserDto("UpdatedUser", "updatedUser@email.com");

        UserDto user = userService.updateUser(updatedUserId, updatedUserDto);
        assertThat(updatedUserId, equalTo(user.getId()));
        assertThat(updatedUserDto.getName(), equalTo(user.getName()));
        assertThat(updatedUserDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    public void shouldDeleteAllUsers() {
        Integer usersAmountBefore = userService.getAllUsers().size();
        assertThat(usersAmountBefore, equalTo(1));

        userService.deleteAllUsers();
        Integer usersAmountAfter = userService.getAllUsers().size();
        assertThat(usersAmountAfter, equalTo(0));
    }

    @Test
    public void shouldDeleteUserById() {
        UserDto deletedUserDto = makeUserDto("deletedUser", "deletedUser@email.com");
        Long deletedUserId = userService.addUser(deletedUserDto).getId();
        Integer usersAmountBefore = userService.getAllUsers().size();
        assertThat(usersAmountBefore, equalTo(2));

        userService.deleteUserById(deletedUserId);
        Integer usersAmountAfter = userService.getAllUsers().size();
        assertThat(usersAmountAfter, equalTo(1));
    }

}
