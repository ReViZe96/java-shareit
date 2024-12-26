package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Mapper
public interface UserMapper {

    User userDtoToUser(UserDto userDto);

    UserDto userToUserDto(User user);

}
