package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

    @MockBean
    BookigService bookigService;


    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    private BookingDto bookingDto;
    private BookingDto updateBookingDto;

    @BeforeEach
    void setUp() {
        bookingDto = new BookingDto(1L, null, null,
                new BookingDto.Item(1L, "секатор садовый"), new BookingDto.User(1L),
                BookingStatus.APPROVED);

        updateBookingDto = new BookingDto(1L, null, null,
                new BookingDto.Item(1L, "секатор садовый Fiskars"), new BookingDto.User(1L), BookingStatus.APPROVED);
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
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updateBookingDto.getId()), Long.class))
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
        when(bookigService.findBookingsByUser(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(bookingDto));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findBookingByOwnerTest() throws Exception {
        when(bookigService.findBookingsByOwner(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(bookingDto));
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
