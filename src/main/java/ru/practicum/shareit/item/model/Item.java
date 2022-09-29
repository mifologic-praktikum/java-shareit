package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    private long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
}
