package ru.practicum.shareit;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = BookingController.class)
@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    @SneakyThrows
    @Test
    public void shouldCreateBookingEndTimeIsAfterStartCase() {

        String json = "{\"itemId\":1,\"start\":\"2023-08-08T01:01:01\",\"end\":\"2023-07-09T02:02:02\"}";

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Дата окончания бронирования должна быть позднее даты начала бронирования"));

        Mockito.verify(bookingClient, Mockito.never()).create(Mockito.anyLong(), Mockito.any());
    }

    @SneakyThrows
    @Test
    public void shouldCreateBookingStartTimeInPastCase() {

        String json = "{\"itemId\":1,\"start\":\"2023-01-08T01:01:01\",\"end\":\"2023-07-09T02:02:02\"}";

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Дата начала аренды должна быть текущей или в " +
                "будущем"));

        Mockito.verify(bookingClient, Mockito.never()).create(Mockito.anyLong(), Mockito.any());
    }

    @SneakyThrows
    @Test
    public void shouldCreateBookingStartTimeNullCase() {

        String json = "{\"itemId\":1,\"start\":null,\"end\":\"2023-07-09T02:02:02\"}";

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Дата начала аренды должна быть указана"));

        Mockito.verify(bookingClient, Mockito.never()).create(Mockito.anyLong(), Mockito.any());
    }
}