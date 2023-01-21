package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.comments.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testSerialize() throws Exception {
        CommentDto commentDto = new CommentDto(1L, "new comment", "Kim Crowley", LocalDateTime.of(2023, 1, 13, 22, 13, 48));
        JsonContent<CommentDto> resultJson = json.write(commentDto);
        assertThat(resultJson).hasJsonPath("$.id");
        assertThat(resultJson).hasJsonPath("$.text");
        assertThat(resultJson).hasJsonPath("$.authorName");
        assertThat(resultJson).hasJsonPath("$.created");
    }
}
