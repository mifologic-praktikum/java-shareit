package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userStorage, UserMapper userMapper) {
        this.userRepository = userStorage;
        this.userMapper = userMapper;
    }


    @Override
    public List<UserDto> findAllUsers() {
        return userMapper.toListUserDto(userRepository.findAll());
    }

    @Override
    public UserDto findUserById(Long userId) {
        return userMapper.toUserDto(userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id= " + userId + " not found")));
    }

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        userRepository.save(user);
        return userMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User userInStorage = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id= " + userId + " not found")
        );
        User user = userMapper.toUser(userDto);
        if (user.getEmail() != null) {
            userInStorage.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userInStorage.setName(user.getName());
        }
        userRepository.save(userInStorage);
        return userMapper.toUserDto(userInStorage);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
