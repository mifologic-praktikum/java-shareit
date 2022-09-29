package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<User> findAllUsers();

    User findUserById(Long userId);

    public User createUser(UserDto userDto);

    public User updateUser(Long userId, UserDto userDto);

    public void deleteUser(Long userId);
}
