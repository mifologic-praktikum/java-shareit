package ru.practicum.shareit.exception;

public class UserHasNoBookings extends RuntimeException {

    public UserHasNoBookings(String s) {
        super(s);
    }
}
