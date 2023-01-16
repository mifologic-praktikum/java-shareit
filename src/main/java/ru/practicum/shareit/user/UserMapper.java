package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserMapper {

    public User toUser(UserDto userDto) {
        return new User(userDto.getId(),
                userDto.getName(),
                userDto.getEmail());
    }

    public UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public List<UserDto> toListUserDto(List<User> usersList) {
        List<UserDto> itemDtos = new ArrayList<>();
        for (User user : usersList) {
            itemDtos.add(toUserDto(user));
        }
        return itemDtos;
    }
}
