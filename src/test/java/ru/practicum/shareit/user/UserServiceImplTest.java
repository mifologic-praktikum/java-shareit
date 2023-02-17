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
public class UserServiceImplTest {

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
    void findAllUsersTest() {
        when(userMapper.toListUserDto(anyList()))
                .thenReturn(Collections.singletonList(userDto));
        List<UserDto> userDtos = userService.findAllUsers();
        assertNotNull(userDtos);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findUserByIdTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(userMapper.toUserDto(any()))
                .thenReturn(userDto);
        UserDto userFound = userService.findUserById(user.getId());
        assertNotNull(userFound);
        assertEquals(1L, userFound.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void userNotFoundTest() {
        assertThrows(NotFoundException.class, () -> userService.findUserById(42L));
    }

    @Test
    void createUserTest() {
        when(userMapper.toUser(any()))
                .thenReturn(user);
        when(userRepository.save(any()))
                .thenReturn(user);
        when(userMapper.toUserDto(any()))
                .thenReturn(userDto);
        UserDto userCreated = userService.createUser(userDto);
        assertNotNull(userCreated);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUserEmailTest() {
        UserDto userDtoUpdate = new UserDto(1L, null, "userUpdate@test.com");
        User userUpdate = new User(1L, null, "userUpdate@test.com");
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userUpdate));
        when(userMapper.toUser(any()))
                .thenReturn(userUpdate);
        when(userMapper.toUserDto(any()))
                .thenReturn(userDtoUpdate);
        UserDto update = userService.updateUser(1L, userDtoUpdate);
        assertEquals("userUpdate@test.com", update.getEmail());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(userUpdate);
    }

    @Test
    void updateUserNameTest() {
        UserDto userDtoUpdate = new UserDto(1L, "userNameUpd", null);
        User userUpdate = new User(1L, "userNameUpd", null);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userUpdate));
        when(userMapper.toUser(any()))
                .thenReturn(userUpdate);
        when(userMapper.toUserDto(any()))
                .thenReturn(userDtoUpdate);
        UserDto update = userService.updateUser(1L, userDtoUpdate);
        assertEquals("userNameUpd", update.getName());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(userUpdate);
    }

    @Test
    void updateUserEmptyNameTest() {
        UserDto userDtoUpdate = new UserDto(1L, "userNameUpd", "userUpdate@test.com");
        User userUpdate = new User(1L, "", "userUpdate@test.com");
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userUpdate));
        when(userMapper.toUser(any()))
                .thenReturn(userUpdate);
        when(userMapper.toUserDto(any()))
                .thenReturn(userDtoUpdate);
        UserDto update = userService.updateUser(1L, userDtoUpdate);
        assertEquals("userUpdate@test.com", update.getEmail());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(userUpdate);
    }

    @Test
    void userUpdateUserNotFound() {
        UserDto userDtoUpdate = new UserDto(1L, "userName", "userUpdate@test.com");
        assertThrows(NotFoundException.class, () -> userService.updateUser(42L, userDtoUpdate));
    }
}
