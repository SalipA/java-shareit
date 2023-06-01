package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingDto;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldHideItemIdFieldThanSerialization() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(2L);
        String expected = "{\"id\":null,\"start\":null,\"end\":null,\"item\":null,\"booker\":null,\"status\":null}";

        String actual = objectMapper.writeValueAsString(bookingDto);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void shouldReadItemIdFieldThanDeserialization() throws Exception {
        String json = "{\"itemId\":2,\"start\":null,\"end\":null}";
        BookingDto expected = new BookingDto();
        expected.setItemId(2L);

        BookingDto actual = objectMapper.readValue(json, BookingDto.class);

        Assertions.assertEquals(actual, expected);
    }
}