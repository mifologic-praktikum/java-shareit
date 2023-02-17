package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ItemRequestRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    User user;
    ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User(1L, "userName", "user@test.com"));
        itemRequest = itemRequestRepository.save(new ItemRequest(1L, "газовая горелка", LocalDateTime.now(), user));
    }

    @Test
    void findAllByRequesterIdOrderByCreatedDescTest() {
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(user.getId());
        assertNotNull(itemRequestList);
    }

    @Test
    void findAllByRequesterIdIsNotOrderByCreatedDescTest() {
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequesterIdIsNotOrderByCreatedDesc(user.getId(), Pageable.unpaged());
        assertNotNull(itemRequestList);
    }

}
