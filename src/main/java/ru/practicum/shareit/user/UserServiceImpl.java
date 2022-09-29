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
    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    @Override
    public User findUserById(Long userId) {
        return userStorage.findUserById(userId);
    }

    @Override
    public User createUser(UserDto userDto) {
        return userStorage.createUser(UserMapper.toUser(userDto));
    }

    @Override
    public User updateUser(Long userId, UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return userStorage.updateUser(userId, user);
    }

    @Override
    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }
}
