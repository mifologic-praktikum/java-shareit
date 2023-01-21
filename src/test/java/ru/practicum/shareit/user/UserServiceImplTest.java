package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest  {

    private UserServiceImpl userService;

    @Mock
    UserRepository userRepository;
    @Mock
    UserMapper userMapper;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, userMapper);
        userDto = new UserDto(1L, "userName", "user@test.com");
        user = new User(1L, "userName", "user@test.com");
    }

    @Test
    void createUserTest() {
        when(userMapper.toUser(userDto))
                .thenReturn(user);
        userService.createUser(userDto);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void findUserByIdTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(userMapper.toUserDto(any()))
                .thenReturn(userDto);
        assertNotNull(userService.findUserById(user.getId()));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void userNotFoundTest() {
        assertThrows(NotFoundException.class, () -> userService.findUserById(42L));
    }

    @Test
    void findAllUsers() {
        when(userMapper.toListUserDto(anyList()))
                .thenReturn(Collections.singletonList(userDto));
        List<UserDto> userDtos = userService.findAllUsers();
        assertNotNull(userDtos);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void updateUserTest() {
        UserDto userDtoUpdate = new UserDto(1L, "userName", "userUpdate@test.com");
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(userMapper.toUser(any()))
                .thenReturn(user);
        when(userMapper.toUserDto(any()))
                .thenReturn(userDtoUpdate);
        UserDto update = userService.updateUser(1L, userDtoUpdate);
        assertEquals("userUpdate@test.com", update.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }


}
