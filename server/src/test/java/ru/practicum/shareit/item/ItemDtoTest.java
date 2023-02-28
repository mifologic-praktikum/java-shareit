package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testSerialize() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "газовая горелка", "подойдёт для всех видов работ", true, null, null, null, null);
        JsonContent<ItemDto> resultJson = json.write(itemDto);
        assertThat(resultJson).hasJsonPath("$.id");
        assertThat(resultJson).hasJsonPath("$.name");
        assertThat(resultJson).hasJsonPath("$.description");
        assertThat(resultJson).hasJsonPath("$.available");
        assertThat(resultJson).hasJsonPath("$.lastBooking");
        assertThat(resultJson).hasJsonPath("$.nextBooking");
        assertThat(resultJson).hasJsonPath("$.comments");
        assertThat(resultJson).hasJsonPath("$.requestId");
    }
}
