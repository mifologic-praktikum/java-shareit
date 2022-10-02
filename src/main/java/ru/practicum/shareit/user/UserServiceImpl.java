package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final InMemoryUserStorage userStorage;

    public UserServiceImpl(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<UserDto> findAllUsers() {
        return UserMapper.toListUserDto(userStorage.findAllUsers());
    }

    @Override
    public UserDto findUserById(Long userId) {
        return UserMapper.toUserDto(userStorage.findUserById(userId));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        return UserMapper.toUserDto(userStorage.createUser(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userStorage.updateUser(userId, user));
    }

    @Override
    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }
}
