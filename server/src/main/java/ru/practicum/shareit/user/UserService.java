package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> findAllUsers();

    UserDto findUserById(Long userId);

    public UserDto createUser(UserDto userDto);

    public UserDto updateUser(Long userId, UserDto userDto);

    public void deleteUser(Long userId);
}
