package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class BookingRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository repository;

    @Test
    public void shouldUpdateBookingStatusStandardCase() {
        User user = new User();
        user.setName("user");
        user.setEmail("user2@mail.ru");

        entityManager.persist(user);

        Item item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(1L);

        entityManager.persist(item);

        Booking oldBooking = new Booking();
        oldBooking.setStart(LocalDateTime.of(2023, 1, 1, 1, 1, 1));
        oldBooking.setEnd(LocalDateTime.of(2023, 1, 2, 2, 2, 2));
        oldBooking.setStatus(BookingStatuses.WAITING);
        oldBooking.setBooker(user);
        oldBooking.setItem(item);

        entityManager.persist(oldBooking);

        repository.updateBookingStatus(1L, BookingStatuses.APPROVED);

        Optional<Booking> actual = repository.findById(1L);
        Assertions.assertTrue(actual.isPresent());
        Assertions.assertEquals(oldBooking.getStart(), actual.get().getStart());
        Assertions.assertEquals(oldBooking.getEnd(), actual.get().getEnd());
        Assertions.assertEquals(BookingStatuses.APPROVED, actual.get().getStatus());

        user.setId(1L);
        item.setId(1L);

        Assertions.assertEquals(oldBooking.getBooker(), actual.get().getBooker());
        Assertions.assertEquals(oldBooking.getItem(), actual.get().getItem());
    }

}
