package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ServerException;

import java.util.*;

@Component
public class InMemoryUserStorage {

    private static Long userId = 0L;
    private final Map<Long, User> users = new TreeMap<>();
    private final Set<String> emails = new HashSet<>();

    private Long generateUserId() {
        return ++userId;
    }

    private void checkEmailUniq(User user) {
        if (emails.contains(user.getEmail())) {
            throw new ServerException("Email must be uniq");
        } else {
            emails.add(user.getEmail());
        }
    }

    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User findUserById(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("User with id= " + userId + " not found");
        }
        return user;
    }

    public User createUser(User user) {
        checkEmailUniq(user);
        user.setId(generateUserId());
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(Long userId, User user) {
        User userInMemory = findUserById(userId);
        if (user.getEmail() != null) {
            emails.remove(userInMemory.getEmail());
            checkEmailUniq(user);
            userInMemory.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userInMemory.setName(user.getName());
        }
        users.put(userId, userInMemory);
        return userInMemory;
    }

    public void deleteUser(Long userId) {
        emails.remove(users.get(userId).getEmail());
        users.remove(userId);
    }
}
