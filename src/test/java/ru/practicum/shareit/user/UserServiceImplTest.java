package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

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
        when(userService.findUserById(anyLong()))
                .thenReturn(userDto);
        userService.findUserById(user.getId());
        verify(userRepository, times(1)).findById(1L);
    }
}
