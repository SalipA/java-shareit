package ru.practicum.shareit;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.States;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ShareItTests {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;

    @Test
    @Order(1)
    void contextLoads() {
    }

    @Test
    @Order(2)
    public void shouldCreateUserStandardCase() {
        UserDto testUserDto = userService.create(new UserDto(0L, "testName", "test@test.ru"));
        Assertions.assertEquals(1L, testUserDto.getId());
        Assertions.assertEquals(userService.read(1L), testUserDto);
        Assertions.assertEquals(1, userService.readAll().size());
    }

    @Test
    @Order(3)
    public void shouldUpdateUserStandardCase() {
        UserDto newUser = new UserDto(1L, "testNameUpdate", "testUpdate@test.ru");
        UserDto updatedUser = userService.update(1L, newUser);
        Assertions.assertEquals(newUser, updatedUser);
        Assertions.assertEquals(userService.read(1L), newUser);
        Assertions.assertEquals(1, userService.readAll().size());
    }

    @Test
    @Order(4)
    public void shouldReadUserStandardCase() {
        UserDto userFromDb = userService.read(1L);
        Assertions.assertEquals(1L, userFromDb.getId());
        Assertions.assertEquals("testNameUpdate", userFromDb.getName());
        Assertions.assertEquals("testUpdate@test.ru", userFromDb.getEmail());
        Assertions.assertEquals(1, userService.readAll().size());
    }

    @Test
    @Order(5)
    public void shouldReadAllUsers() {
        userService.create(new UserDto(0L, "testName2", "test2@test.ru"));
        Assertions.assertEquals(2, userService.readAll().size());
    }


    @Test
    @Order(6)
    public void shouldDeleteUserStandardCase() {
        userService.delete(2L);
        Assertions.assertEquals(1, userService.readAll().size());
        final UserNotFoundException exp = Assertions.assertThrows(UserNotFoundException.class,
            () -> userService.read(2L)
        );
        Assertions.assertEquals("Пользователь с userId = 2 не найден!",
            exp.getMessage());
    }

    @Test
    @Order(7)
    public void shouldCreateItemStandardCase() {
        ItemDto testItemDto = itemService.create(1L, new ItemDto(0L, "тестовая вещь", "большая",
            true, null));
        Assertions.assertEquals(1L, testItemDto.getId());
        Assertions.assertEquals("тестовая вещь", testItemDto.getName());
        Assertions.assertEquals("большая", testItemDto.getDescription());
        Assertions.assertTrue(testItemDto.getAvailable());
    }

    @Test
    @Order(8)
    public void shouldUpdateItemStandardCase() {
        ItemDto itemNew = new ItemDto(1L, "тестовая вещь измененная", "большая имененная",
            true, null);
        ItemDto itemFromDb = itemService.update(1L, 1L, itemNew);
        Assertions.assertEquals(itemFromDb.getName(), "тестовая вещь измененная");
        Assertions.assertEquals(itemFromDb.getDescription(), "большая имененная");
        Assertions.assertEquals(itemFromDb.getAvailable(), true);
        Assertions.assertEquals(itemService.readAllByUserId(1L, null,null).size(), 1);
    }

    @Test
    @Order(9)
    public void shouldReadItemStandardCase() {
        Assertions.assertEquals(itemService.read(1L, 1L).getName(), "тестовая вещь измененная");
        Assertions.assertEquals(itemService.read(1L, 1L).getDescription(), "большая имененная");
        Assertions.assertEquals(itemService.read(1L, 1L).getAvailable(), true);
    }

    @Test
    @Order(10)
    public void shouldReadAllItemByUserIdStandardCase() {
        ItemDto itemNew = new ItemDto(0L, "вещь большая", "измененная",
            true, null);
        itemService.create(1L, itemNew);
        List<ItemDto> items = itemService.readAllByUserId(1L, null,null);
        Assertions.assertEquals(2, items.size());
        Assertions.assertEquals(1L, items.get(0).getId());
        Assertions.assertEquals(2L, items.get(1).getId());
    }

    @Test
    @Order(11)
    public void shouldSearchItemStandardCase() {
        String text = "большая";
        List<ItemDto> items = itemService.searchItems(text, null, null);
        Assertions.assertEquals(2, items.size());
        Assertions.assertEquals(1L, items.get(0).getId());
        Assertions.assertEquals(2L, items.get(1).getId());
    }

    @Test
    @Order(12)
    public void shouldCreateBookingStandardCase() {
        userService.create(new UserDto(0L, "testName2", "test2@test.ru"));
        BookingDto testBookingDto = bookingService.create(3L, new BookingDto(1L, LocalDateTime.of(2023,
            6, 7, 10, 10, 10), LocalDateTime.of(2023, 6, 7,
            20, 10, 10)));
        Assertions.assertEquals(1L, testBookingDto.getId());
        Assertions.assertEquals(3L, testBookingDto.getBooker().getId());
    }

    @Test
    @Order(13)
    public void shouldReadBookingStandardCase() {
        BookingDto testBookingDto = bookingService.read(3L, 1L);
        Assertions.assertEquals(1L, testBookingDto.getId());
        Assertions.assertEquals(LocalDateTime.of(2023, 6,
            7, 10, 10, 10), testBookingDto.getStart());
        Assertions.assertEquals(LocalDateTime.of(2023, 6,
            7, 20, 10, 10), testBookingDto.getEnd());
        Assertions.assertEquals(BookingStatuses.WAITING, testBookingDto.getStatus());
    }

    @Test
    @Order(14)
    public void shouldApproveBookingStandardCase() {
        bookingService.approveBooking(1L, 1L, true);
        BookingDto testBookingDto = bookingService.read(3L, 1L);
        Assertions.assertEquals(BookingStatuses.APPROVED, testBookingDto.getStatus());
    }

    @Test
    @Order(15)
    public void shouldGetAllByStateStandardCase() {
        List<BookingDto> bookings = bookingService.getAllByState(3L, States.FUTURE, null,null);
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(1L, bookings.get(0).getId());
        Assertions.assertEquals(LocalDateTime.of(2023, 6,
            7, 10, 10, 10), bookings.get(0).getStart());
        Assertions.assertEquals(LocalDateTime.of(2023, 6,
            7, 20, 10, 10), bookings.get(0).getEnd());
        Assertions.assertEquals(BookingStatuses.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    @Order(16)
    public void shouldGetAllByOwnerAndStateStandardCase() {
        List<BookingDto> bookings = bookingService.getAllByOwnerAndState(1L, States.ALL, null, null);
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(1L, bookings.get(0).getId());
        Assertions.assertEquals(LocalDateTime.of(2023, 6,
            7, 10, 10, 10), bookings.get(0).getStart());
        Assertions.assertEquals(LocalDateTime.of(2023, 6,
            7, 20, 10, 10), bookings.get(0).getEnd());
        Assertions.assertEquals(BookingStatuses.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    @Order(17)
    public void shouldGetItemWithBookingOwnerCase() {
        ItemDto itemDto = itemService.read(1L, 1L);
        Assertions.assertNull(itemDto.getLastBooking());
        Assertions.assertEquals(1L, itemDto.getNextBooking().getId());
    }

    @Test
    @Order(18)
    public void shouldGetItemWithoutBookingNotOwnerCase() {
        ItemDto itemDto = itemService.read(1L, 3L);
        Assertions.assertNull(itemDto.getLastBooking());
        Assertions.assertNull(itemDto.getNextBooking());
    }

    @Test
    @Order(19)
    public void shouldGetAllByStateBookingFrom2SizeNullCase() {
        PaginationParamException exp = Assertions.assertThrows(PaginationParamException.class,
            () -> bookingService.getAllByState(3L,
                States.FUTURE, 2, null));
        Assertions.assertEquals("Не возможно обработать запрос с переданными параметрами пагинации: from = 2," +
            " size = " + "null", exp.getMessage());
    }

    @Test
    @Order(20)
    public void shouldSearchItemFromNullSize2Case() {
        String text = "большая";
        PaginationParamException exp = Assertions.assertThrows(PaginationParamException.class,
            () -> itemService.searchItems(text, null, 2));
        Assertions.assertEquals("Не возможно обработать запрос с переданными параметрами пагинации: from = null, size" +
            " = 2", exp.getMessage());
    }

    @Test
    @Order(21)
    public void shouldSearchItemFromN2SizeNullCase() {
        String text = "большая";
        PaginationParamException exp = Assertions.assertThrows(PaginationParamException.class,
            () -> itemService.searchItems(text, 2, null));
        Assertions.assertEquals("Не возможно обработать запрос с переданными параметрами пагинации: from = 2, size" +
            " = null", exp.getMessage());
    }

    @Test
    @Order(22)
    public void shouldGetAllByStateBookingFromNullSize2Case() {
        PaginationParamException exp = Assertions.assertThrows(PaginationParamException.class,
            () -> bookingService.getAllByState(3L,
                States.FUTURE, null, 2));
        Assertions.assertEquals("Не возможно обработать запрос с переданными параметрами пагинации: from = null," +
            " size = " + "2", exp.getMessage());
    }

    @Test
    @Order(23)
    public void shouldGetAllByStateBookingFrom0Size2Case() {
        List<BookingDto> bookings = bookingService.getAllByState(3L, States.FUTURE, 0, 2);
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals(1L, bookings.get(0).getId());
        Assertions.assertEquals(LocalDateTime.of(2023, 6,
            7, 10, 10, 10), bookings.get(0).getStart());
        Assertions.assertEquals(LocalDateTime.of(2023, 6,
            7, 20, 10, 10), bookings.get(0).getEnd());
        Assertions.assertEquals(BookingStatuses.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    @Order(24)
    public void shouldSearchItemFrom0Size2Case() {
        String text = "большая";
        List<ItemDto> items = itemService.searchItems(text, 0, 2);
        Assertions.assertEquals(2, items.size());
        Assertions.assertEquals(1L, items.get(0).getId());
        Assertions.assertEquals(2L, items.get(1).getId());
    }
}