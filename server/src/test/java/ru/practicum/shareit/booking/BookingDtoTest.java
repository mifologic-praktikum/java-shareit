package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testSerialize() throws Exception {
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.of(2023, 12, 12, 13, 48, 48),
                LocalDateTime.of(2023, 12, 22, 13, 48, 48),
                new BookingDto.Item(1L, "газовая горелка"), new BookingDto.User(1L),
                BookingStatus.APPROVED);
        JsonContent<BookingDto> resultJson = json.write(bookingDto);
        assertThat(resultJson).hasJsonPath("$.id");
        assertThat(resultJson).hasJsonPath("$.start");
        assertThat(resultJson).hasJsonPath("$.end");
        assertThat(resultJson).hasJsonPath("$.item");
        assertThat(resultJson).hasJsonPath("$.booker");
        assertThat(resultJson).hasJsonPath("$.status");

    }


}
