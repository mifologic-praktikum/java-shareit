package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        ItemRequestDto itemRequestDto =  new ItemRequestDto(1L, "газовая горелка", LocalDateTime.now(), null);
        JsonContent<ItemRequestDto> resultJson = json.write(itemRequestDto);
        assertThat(resultJson).hasJsonPath("$.id");
        assertThat(resultJson).hasJsonPath("$.description");
        assertThat(resultJson).extractingJsonPathValue("$.description").isEqualTo(itemRequestDto.getDescription());
    }


}
