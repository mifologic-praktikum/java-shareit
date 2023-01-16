package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BookingControllerTest {

    @Mock
    BookigService bookigService;

    @InjectMocks
    BookingController bookingController;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private BookingDto bookingDto;
    private BookingDto updateBookingDto;
    private User user;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        bookingDto = new BookingDto(1L, null, null,
                new BookingDto.Item(1L, "секатор садовый"), new BookingDto.User(1L),
                BookingStatus.APPROVED);

        updateBookingDto = new BookingDto(1L, null, null,
                new BookingDto.Item(1L, "секатор садовый Fiskars"), new BookingDto.User(1L), BookingStatus.CANCELED);

        user = new User(1L, "userName", "user@test.com");

    }

    @Test
    void createBookingTest() throws Exception {
        when(bookigService.createBooking(anyLong(), any()))
                .thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));
    }

    @Test
    void updateBookingTest() throws Exception {
        when(bookigService.updateBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(updateBookingDto);
        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(updateBookingDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updateBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(updateBookingDto.getStatus())))
                .andExpect(jsonPath("$.item.name", is(updateBookingDto.getItem().getName())));
    }

    @Test
    void findBookingByIdTest() throws Exception {
        when(bookigService.findBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDto);
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())));
    }

    @Test
    void findBookingByUserTest() throws Exception {
        when(bookigService.findBookingByUser(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(bookingDto));
        mvc.perform(get("/bookings/owner")
                .header("X-Sharer-User-Id", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
