package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    private UserServiceImpl userService;

    UserRepository userRepository;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
        userDto = new UserDto(1L, "userName", "user@test.com");
        user = new User(1L, "userName", "user@test.com");
    }

    @Test
    void findAllUsersTest() {
        when(userRepository.findAll())
                .thenReturn(Collections.singletonList(user));
        List<UserDto> userDtos = userService.findAllUsers();
        assertNotNull(userDtos);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findUserByIdTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        UserDto userFound = userService.findUserById(user.getId());
        assertNotNull(userFound);
        assertEquals(1L, userFound.getId());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void userNotFoundTest() {
        assertThrows(NotFoundException.class, () -> userService.findUserById(42L));
    }

    @Test
    void createUserTest() {
        when(userRepository.save(any()))
                .thenReturn(user);
        UserDto userCreated = userService.createUser(userDto);
        assertNotNull(userCreated);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void updateUserEmailTest() {
        UserDto userDtoUpdate = new UserDto(1L, null, "userUpdate@test.com");
        User userUpdate = new User(1L, null, "userUpdate@test.com");
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userUpdate));
        UserDto update = userService.updateUser(1L, userDtoUpdate);
        assertEquals("userUpdate@test.com", update.getEmail());
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void updateUserNameTest() {
        UserDto userDtoUpdate = new UserDto(1L, "userNameUpd", null);
        User userUpdate = new User(1L, "userNameUpd", null);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userUpdate));
        UserDto update = userService.updateUser(1L, userDtoUpdate);
        assertEquals("userNameUpd", update.getName());
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void updateUserEmptyNameTest() {
        UserDto userDtoUpdate = new UserDto(1L, "userNameUpd", "userUpdate@test.com");
        User userUpdate = new User(1L, "", "userUpdate@test.com");
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userUpdate));
        UserDto update = userService.updateUser(1L, userDtoUpdate);
        assertEquals("userUpdate@test.com", update.getEmail());
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void userUpdateUserNotFound() {
        UserDto userDtoUpdate = new UserDto(1L, "userName", "userUpdate@test.com");
        assertThrows(NotFoundException.class, () -> userService.updateUser(42L, userDtoUpdate));
    }
}
