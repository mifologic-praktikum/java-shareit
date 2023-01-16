package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.Create;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ItemRequestDto {

    private Long id;
    @NotBlank(groups = {Create.class})
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class ItemDto {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
        private  Long requestId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class User {
        Long id;
    }
}
