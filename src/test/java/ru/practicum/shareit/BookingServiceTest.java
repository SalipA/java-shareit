package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingReadAccessException;
import ru.practicum.shareit.booking.exception.BookingStatusAlreadyChangedException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.service.States;
import ru.practicum.shareit.item.exception.ItemEditAccessException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserService userService;
    @Mock
    ItemService itemService;
    @Mock
    BookingMapper bookingMapper;
    @InjectMocks
    BookingServiceImpl bookingService;

    @Test
    public void shouldCreateBookingStandardCase() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(2L);
        bookingDto.setStart(LocalDateTime.of(2023, 1, 1, 1, 1, 1));
        bookingDto.setEnd(LocalDateTime.of(2023, 1, 2, 2, 2, 2));

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2023, 1, 1, 1, 1, 1));
        booking.setEnd(LocalDateTime.of(2023, 1, 2, 2, 2, 2));
        booking.setStatus(BookingStatuses.WAITING);


        Mockito.when(bookingMapper.toBooking(Mockito.any())).thenReturn(booking);
        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(itemService.checkItemIsAvailableForBooking(Mockito.anyLong(), Mockito.anyLong())).thenReturn(new Item());
        Mockito.when(bookingRepository.save(Mockito.any())).thenReturn(booking);

        BookingDto actual = bookingService.create(1L, bookingDto);

        Mockito.verify(bookingRepository, Mockito.times(1)).save(booking);
        Assertions.assertEquals(BookingStatuses.WAITING, actual.getStatus());
        Assertions.assertEquals(booking.getStart(), actual.getStart());
        Assertions.assertEquals(booking.getEnd(), actual.getEnd());
    }

    @Test
    public void shouldReadBookingNotFoundCase() {
        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenThrow(new BookingNotFoundException(0L));

        Assertions.assertThrows(BookingNotFoundException.class,
            () -> bookingService.read(0L, 0L));
    }

    @Test
    public void shouldReadBookingUserNoAccessCase() {
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setOwner(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2023, 1, 1, 1, 1, 1));
        booking.setEnd(LocalDateTime.of(2023, 1, 2, 2, 2, 2));
        booking.setStatus(BookingStatuses.WAITING);
        booking.setBooker(user);
        booking.setItem(item);

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

        Assertions.assertThrows(BookingReadAccessException.class, () -> bookingService.read(3L, 1L));
    }

    @Test
    public void shouldReadStandardCase() {
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setOwner(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2023, 1, 1, 1, 1, 1));
        booking.setEnd(LocalDateTime.of(2023, 1, 2, 2, 2, 2));
        booking.setStatus(BookingStatuses.WAITING);
        booking.setBooker(user);
        booking.setItem(item);

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

        BookingDto actual = bookingService.read(2L, 1L);

        Assertions.assertEquals(booking.getId(), actual.getId());
        Assertions.assertEquals(booking.getStart(), actual.getStart());
        Assertions.assertEquals(booking.getEnd(), actual.getEnd());
        Assertions.assertEquals(booking.getStatus(), actual.getStatus());
    }

    @Test
    public void shouldApproveBookingUserNoAccessCase() {
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setOwner(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

        Assertions.assertThrows(ItemEditAccessException.class,
            () -> bookingService.approveBooking(0L, 1L, true));
    }

    @Test
    public void shouldApproveBookingBookingNotFoundCase() {

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenThrow(BookingNotFoundException.class);

        Assertions.assertThrows(BookingNotFoundException.class,
            () -> bookingService.approveBooking(0L, 1L, true));
    }

    @Test
    public void shouldApproveBookingStatusHasBeenChangedCase() {
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setOwner(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatuses.REJECTED);

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

        Assertions.assertThrows(BookingStatusAlreadyChangedException.class,
            () -> bookingService.approveBooking(2L, 1L, true));
    }

    @Test
    public void shouldApproveBookingStatusStandardCase() {
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setOwner(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatuses.WAITING);

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

        BookingDto actual = bookingService.approveBooking(2L, 1L, true);
        Assertions.assertEquals(BookingStatuses.APPROVED, actual.getStatus());
    }

    @Test
    public void shouldNotApproveBookingStatusStandardCase() {
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setOwner(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatuses.WAITING);

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

        BookingDto actual = bookingService.approveBooking(2L, 1L, false);
        Assertions.assertEquals(BookingStatuses.REJECTED, actual.getStatus());
    }

    @Test
    public void shouldGetAllByStatePastCase() {
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setOwner(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);

        List<Booking> bookings = List.of(booking);
        Page<Booking> pagedResponse = new PageImpl<>(bookings);

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(Mockito.anyLong(),
            Mockito.any(), Mockito.any())).thenReturn(pagedResponse);

        List<BookingDto> actual = bookingService.getAllByState(1L, States.PAST, null, null);
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(booking.getId(), actual.get(0).getId());
        Assertions.assertEquals(booking.getBooker().getId(), actual.get(0).getBooker().getId());
    }

    @Test
    public void shouldGetAllByStateCurrentCase() {
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setOwner(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);

        List<Booking> bookings = List.of(booking);
        Page<Booking> pagedResponse = new PageImpl<>(bookings);

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(bookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Mockito.any(),
            Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(pagedResponse);

        List<BookingDto> actual = bookingService.getAllByState(1L, States.CURRENT, null, null);
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(booking.getId(), actual.get(0).getId());
        Assertions.assertEquals(booking.getBooker().getId(), actual.get(0).getBooker().getId());
    }

    @Test
    public void shouldGetAllByStateWaitingCase() {
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setOwner(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);

        List<Booking> bookings = List.of(booking);
        Page<Booking> pagedResponse = new PageImpl<>(bookings);

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(Mockito.any(),
            Mockito.any(), Mockito.any())).thenReturn(pagedResponse);

        List<BookingDto> actual = bookingService.getAllByState(1L, States.WAITING, null, null);
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(booking.getId(), actual.get(0).getId());
        Assertions.assertEquals(booking.getBooker().getId(), actual.get(0).getBooker().getId());
    }

    @Test
    public void shouldGetAllByStateRejectedCase() {
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setOwner(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);

        List<Booking> bookings = List.of(booking);
        Page<Booking> pagedResponse = new PageImpl<>(bookings);

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(Mockito.any(),
            Mockito.any(), Mockito.any())).thenReturn(pagedResponse);

        List<BookingDto> actual = bookingService.getAllByState(1L, States.REJECTED, null, null);
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(booking.getId(), actual.get(0).getId());
        Assertions.assertEquals(booking.getBooker().getId(), actual.get(0).getBooker().getId());
    }

    @Test
    public void shouldGetAllByOwnerAndStateFutureCase() {
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setOwner(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);

        List<Booking> bookings = List.of(booking);
        Page<Booking> pagedResponse = new PageImpl<>(bookings);

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(itemService.findItemsByOwner(Mockito.anyLong())).thenReturn(List.of(item));
        Mockito.when(bookingRepository.findAllByItemInAndStartGreaterThanEqualOrderByStartDesc(Mockito.any(),
            Mockito.any(), Mockito.any())).thenReturn(pagedResponse);

        List<BookingDto> actual = bookingService.getAllByOwnerAndState(2L, States.FUTURE, null, null);
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(booking.getId(), actual.get(0).getId());
        Assertions.assertEquals(booking.getBooker().getId(), actual.get(0).getBooker().getId());
    }

    @Test
    public void shouldGetAllByOwnerAndStateAllCase() {
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setOwner(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);

        List<Booking> bookings = List.of(booking);
        Page<Booking> pagedResponse = new PageImpl<>(bookings);

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(itemService.findItemsByOwner(Mockito.anyLong())).thenReturn(List.of(item));
        Mockito.when(bookingRepository.findAllByItemInOrderByStartDesc(Mockito.any(),
            Mockito.any())).thenReturn(pagedResponse);

        List<BookingDto> actual = bookingService.getAllByOwnerAndState(2L, States.ALL, null, null);
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(booking.getId(), actual.get(0).getId());
        Assertions.assertEquals(booking.getBooker().getId(), actual.get(0).getBooker().getId());
    }

    @Test
    public void shouldGetAllByOwnerAndStatePastCase() {
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setOwner(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);

        List<Booking> bookings = List.of(booking);
        Page<Booking> pagedResponse = new PageImpl<>(bookings);

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(itemService.findItemsByOwner(Mockito.anyLong())).thenReturn(List.of(item));
        Mockito.when(bookingRepository.findAllByItemInAndEndLessThanEqualOrderByStartDesc(Mockito.any(),
            Mockito.any(), Mockito.any())).thenReturn(pagedResponse);

        List<BookingDto> actual = bookingService.getAllByOwnerAndState(2L, States.PAST, null, null);
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(booking.getId(), actual.get(0).getId());
        Assertions.assertEquals(booking.getBooker().getId(), actual.get(0).getBooker().getId());
    }

    @Test
    public void shouldGetAllByOwnerAndStateCurrentCase() {
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setOwner(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);

        List<Booking> bookings = List.of(booking);
        Page<Booking> pagedResponse = new PageImpl<>(bookings);

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(itemService.findItemsByOwner(Mockito.anyLong())).thenReturn(List.of(item));
        Mockito.when(bookingRepository.findAllByItemInAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(Mockito.any(),
            Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(pagedResponse);

        List<BookingDto> actual = bookingService.getAllByOwnerAndState(2L, States.CURRENT, null, null);
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(booking.getId(), actual.get(0).getId());
        Assertions.assertEquals(booking.getBooker().getId(), actual.get(0).getBooker().getId());
    }

    @Test
    public void shouldGetAllByOwnerAndStateWaitingCase() {
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setOwner(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);

        List<Booking> bookings = List.of(booking);
        Page<Booking> pagedResponse = new PageImpl<>(bookings);

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(itemService.findItemsByOwner(Mockito.anyLong())).thenReturn(List.of(item));
        Mockito.when(bookingRepository.findAllByItemInAndStatusOrderByStartDesc(Mockito.any(),
            Mockito.any(), Mockito.any())).thenReturn(pagedResponse);

        List<BookingDto> actual = bookingService.getAllByOwnerAndState(2L, States.WAITING, null, null);
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(booking.getId(), actual.get(0).getId());
        Assertions.assertEquals(booking.getBooker().getId(), actual.get(0).getBooker().getId());
    }

    @Test
    public void shouldGetAllByOwnerAndStateRejectedCase() {
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setOwner(2L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
        booking.setItem(item);

        List<Booking> bookings = List.of(booking);
        Page<Booking> pagedResponse = new PageImpl<>(bookings);

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(itemService.findItemsByOwner(Mockito.anyLong())).thenReturn(List.of(item));
        Mockito.when(bookingRepository.findAllByItemInAndStatusOrderByStartDesc(Mockito.any(),
            Mockito.any(), Mockito.any())).thenReturn(pagedResponse);

        List<BookingDto> actual = bookingService.getAllByOwnerAndState(2L, States.REJECTED, null, null);
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals(booking.getId(), actual.get(0).getId());
        Assertions.assertEquals(booking.getBooker().getId(), actual.get(0).getBooker().getId());
    }

    @Test
    public void shouldReadBookingEmptyCase() {

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(BookingNotFoundException.class, () -> bookingService.read(2L, 1L));
    }
}
