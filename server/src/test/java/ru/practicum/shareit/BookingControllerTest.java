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
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.States;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = BookingController.class)
@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;

    @SneakyThrows
    @Test
    public void shouldCreateStandardCase() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.of(2023, 8, 8, 1, 1, 1));
        bookingDto.setEnd(LocalDateTime.of(2023, 9, 9, 2, 2, 2));
        bookingDto.setStatus(BookingStatuses.WAITING);

        String json = "{\"itemId\":1,\"start\":\"2023-08-08T01:01:01\",\"end\":\"2023-09-09T02:02:02\"}";

        Mockito.when(bookingService.create(Mockito.anyLong(), Mockito.any())).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
            .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
            .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));

        Mockito.verify(bookingService, Mockito.times(1)).create(Mockito.anyLong(), Mockito.any());
    }

    @SneakyThrows
    @Test
    public void shouldReadStandardCase() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.of(2023, 8, 8, 1, 1, 1));
        bookingDto.setEnd(LocalDateTime.of(2023, 9, 9, 2, 2, 2));
        bookingDto.setStatus(BookingStatuses.WAITING);

        Mockito.when(bookingService.read(Mockito.anyLong(), Mockito.anyLong())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(bookingDto.getId()))
            .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
            .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
            .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
            .andExpect(jsonPath("$.item").hasJsonPath())
            .andExpect(jsonPath("$.booker").hasJsonPath());

        Mockito.verify(bookingService, Mockito.times(1)).read(Mockito.anyLong(), Mockito.any());
    }

    @SneakyThrows
    @Test
    public void shouldApproveBookingStandardCase() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.of(2023, 8, 8, 1, 1, 1));
        bookingDto.setEnd(LocalDateTime.of(2023, 9, 9, 2, 2, 2));
        bookingDto.setStatus(BookingStatuses.APPROVED);

        Mockito.when(bookingService.approveBooking(1L, 1L, true)).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                .header("X-Sharer-User-Id", 1)
                .param("approved", "true"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(bookingDto.getId()))
            .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
            .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
            .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
            .andExpect(jsonPath("$.item").hasJsonPath())
            .andExpect(jsonPath("$.booker").hasJsonPath());

        Mockito.verify(bookingService, Mockito.times(1)).approveBooking(1L, 1L, true);
    }

    @SneakyThrows
    @Test
    public void shouldNotApproveBookingStandardCase() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.of(2023, 8, 8, 1, 1, 1));
        bookingDto.setEnd(LocalDateTime.of(2023, 9, 9, 2, 2, 2));
        bookingDto.setStatus(BookingStatuses.REJECTED);

        Mockito.when(bookingService.approveBooking(1L, 1L, false)).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                .header("X-Sharer-User-Id", 1)
                .param("approved", "false"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(bookingDto.getId()))
            .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
            .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
            .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
            .andExpect(jsonPath("$.item").hasJsonPath())
            .andExpect(jsonPath("$.booker").hasJsonPath());

        Mockito.verify(bookingService, Mockito.times(1)).approveBooking(1L, 1L, false);
    }

    @SneakyThrows
    @Test
    public void shouldApproveBookingApprovedNullCase() {
        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isBadRequest());
        Mockito.verify(bookingService, Mockito.never()).approveBooking(Mockito.anyLong(), Mockito.anyLong(),
            Mockito.any());
    }

    @SneakyThrows
    @Test
    public void shouldGetAllByStateALLCase() {
        int from = 0;
        int size = 1;

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.of(2023, 8, 8, 1, 1, 1));
        bookingDto.setEnd(LocalDateTime.of(2023, 9, 9, 2, 2, 2));
        bookingDto.setStatus(BookingStatuses.APPROVED);

        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setId(1L);
        bookingDto2.setItemId(1L);
        bookingDto2.setStart(LocalDateTime.of(2023, 10, 8, 1, 1, 1));
        bookingDto2.setEnd(LocalDateTime.of(2023, 12, 9, 2, 2, 2));
        bookingDto2.setStatus(BookingStatuses.APPROVED);

        List<BookingDto> bookingDtoList = List.of(bookingDto, bookingDto2);
        Mockito.when(bookingService.getAllByState(1L, States.ALL, from, size)).thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", 1)
                .param("from", Integer.toString(from))
                .param("size", Integer.toString(size)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
            .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
            .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
            .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
            .andExpect(jsonPath("$[1].id").value(bookingDto2.getId()))
            .andExpect(jsonPath("$[1].start", is(bookingDto2.getStart().toString())))
            .andExpect(jsonPath("$[1].end", is(bookingDto2.getEnd().toString())))
            .andExpect(jsonPath("$[1].status", is(bookingDto2.getStatus().toString())));
    }

    @SneakyThrows
    @Test
    public void shouldGetAllByStateALLOwnerCase() {
        int from = 0;
        int size = 1;

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.of(2023, 8, 8, 1, 1, 1));
        bookingDto.setEnd(LocalDateTime.of(2023, 9, 9, 2, 2, 2));
        bookingDto.setStatus(BookingStatuses.APPROVED);

        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setId(1L);
        bookingDto2.setItemId(1L);
        bookingDto2.setStart(LocalDateTime.of(2023, 10, 8, 1, 1, 1));
        bookingDto2.setEnd(LocalDateTime.of(2023, 12, 9, 2, 2, 2));
        bookingDto2.setStatus(BookingStatuses.APPROVED);

        List<BookingDto> bookingDtoList = List.of(bookingDto, bookingDto2);
        Mockito.when(bookingService.getAllByOwnerAndState(1L, States.ALL, from, size)).thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings/owner")
                .header("X-Sharer-User-Id", 1)
                .param("from", Integer.toString(from))
                .param("size", Integer.toString(size)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
            .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
            .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
            .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
            .andExpect(jsonPath("$[1].id").value(bookingDto2.getId()))
            .andExpect(jsonPath("$[1].start", is(bookingDto2.getStart().toString())))
            .andExpect(jsonPath("$[1].end", is(bookingDto2.getEnd().toString())))
            .andExpect(jsonPath("$[1].status", is(bookingDto2.getStatus().toString())));
    }

    @SneakyThrows
    @Test
    public void shouldGetAllByStateFutureCase() {
        int from = 0;
        int size = 1;

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.of(2023, 8, 8, 1, 1, 1));
        bookingDto.setEnd(LocalDateTime.of(2023, 9, 9, 2, 2, 2));
        bookingDto.setStatus(BookingStatuses.APPROVED);

        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setId(1L);
        bookingDto2.setItemId(1L);
        bookingDto2.setStart(LocalDateTime.of(2023, 10, 8, 1, 1, 1));
        bookingDto2.setEnd(LocalDateTime.of(2023, 12, 9, 2, 2, 2));
        bookingDto2.setStatus(BookingStatuses.APPROVED);

        List<BookingDto> bookingDtoList = List.of(bookingDto, bookingDto2);
        Mockito.when(bookingService.getAllByState(1L, States.FUTURE, from, size)).thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", 1)
                .param("state", "FUTURE")
                .param("from", Integer.toString(from))
                .param("size", Integer.toString(size)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
            .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
            .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
            .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
            .andExpect(jsonPath("$[1].id").value(bookingDto2.getId()))
            .andExpect(jsonPath("$[1].start", is(bookingDto2.getStart().toString())))
            .andExpect(jsonPath("$[1].end", is(bookingDto2.getEnd().toString())))
            .andExpect(jsonPath("$[1].status", is(bookingDto2.getStatus().toString())));
    }

    @SneakyThrows
    @Test
    public void shouldGetAllByStateFutureOwnerCase() {
        int from = 0;
        int size = 1;

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.of(2023, 8, 8, 1, 1, 1));
        bookingDto.setEnd(LocalDateTime.of(2023, 9, 9, 2, 2, 2));
        bookingDto.setStatus(BookingStatuses.APPROVED);

        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setId(1L);
        bookingDto2.setItemId(1L);
        bookingDto2.setStart(LocalDateTime.of(2023, 10, 8, 1, 1, 1));
        bookingDto2.setEnd(LocalDateTime.of(2023, 12, 9, 2, 2, 2));
        bookingDto2.setStatus(BookingStatuses.APPROVED);

        List<BookingDto> bookingDtoList = List.of(bookingDto, bookingDto2);
        Mockito.when(bookingService.getAllByOwnerAndState(1L, States.FUTURE, from, size)).thenReturn(bookingDtoList);
        mockMvc.perform(get("/bookings/owner")
                .header("X-Sharer-User-Id", 1)
                .param("state", "FUTURE")
                .param("from", Integer.toString(from))
                .param("size", Integer.toString(size)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
            .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString())))
            .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString())))
            .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())))
            .andExpect(jsonPath("$[1].id").value(bookingDto2.getId()))
            .andExpect(jsonPath("$[1].start", is(bookingDto2.getStart().toString())))
            .andExpect(jsonPath("$[1].end", is(bookingDto2.getEnd().toString())))
            .andExpect(jsonPath("$[1].status", is(bookingDto2.getStatus().toString())));
    }
}
