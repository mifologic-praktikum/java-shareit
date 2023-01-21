package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testSerialize() throws Exception {
        UserDto userDto = new UserDto(1L, "Beth", "bgibbons@pth.uk");
        JsonContent<UserDto> resultJson = json.write(userDto);
        assertThat(resultJson).hasJsonPath("$.id");
        assertThat(resultJson).hasJsonPath("$.name");
        assertThat(resultJson).hasJsonPath("$.email");
        assertThat(resultJson).extractingJsonPathValue("$.name").isEqualTo(userDto.getName());
        assertThat(resultJson).extractingJsonPathValue("$.email").isEqualTo(userDto.getEmail());
    }


}
