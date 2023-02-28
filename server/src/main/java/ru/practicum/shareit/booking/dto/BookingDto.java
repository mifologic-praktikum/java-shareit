package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookingDto {

    private Long id;
    @NotBlank(groups = {Create.class})
    private LocalDateTime start;
    @NotBlank(groups = {Create.class})
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus status;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Item {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class User {
        private Long id;
    }

}
