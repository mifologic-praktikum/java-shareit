package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockBean
    UserService userService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private UserDto userDto;
    private UserDto updateUser;

    @BeforeEach
    void setUp() {

        userDto = new UserDto(1L, "userName", "user@test.com");
        updateUser = new UserDto(1L, "userName", "updateUser@test.com");
    }

    @Test
    void createUserTest() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users/")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
        verify(userService, times(1))
                .createUser(any());
    }

    @Test
    void findUserByIdTest() throws Exception {
        when(userService.findUserById(anyLong()))
                .thenReturn(userDto);
        mvc.perform(get("/users/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
        verify(userService, times(1))
                .findUserById(1L);
    }

    @Test
    void findAllUsersTest() throws Exception {
        when(userService.findAllUsers())
                .thenReturn(new ArrayList<>(Collections.singletonList(userDto)));
        mvc.perform(get("/users")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService, times(1))
                .findAllUsers();
    }

    @Test
    void updateUserTest() throws Exception {
        when(userService.updateUser(anyLong(), any()))
                .thenReturn(updateUser);
        mvc.perform(patch("/users/1")
                .header("X-Sharer-User-Id", 1L)
                .content(mapper.writeValueAsString(updateUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updateUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updateUser.getName())))
                .andExpect(jsonPath("$.email", is(updateUser.getEmail())));
        verify(userService, times(1))
                .updateUser(anyLong(), any());
    }

    @Test
    void deleteUserTest() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
        verify(userService, times(1))
                .deleteUser(1L);
    }
}
