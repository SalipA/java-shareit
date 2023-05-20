package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Modifying
    @Transactional
    @Query("update Booking b set b.status = :status where b.id = :id")
    void updateBookingStatus(@Param(value = "id") Long id, @Param(value = "status") BookingStatuses bookingStatuses);

    List<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime dateTime);

    List<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime dateTime);

    List<Booking> findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId,
                                                                              LocalDateTime startDateTime,
                                                                              LocalDateTime endDateTime);

    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatuses bookingStatus);

    List<Booking> findAllByItemInOrderByStartDesc(List<Item> items);

    List<Booking> findAllByItemInAndStartGreaterThanEqualOrderByStartDesc(List<Item> items, LocalDateTime dateTime);

    List<Booking> findAllByItemInAndEndLessThanEqualOrderByStartDesc(List<Item> items, LocalDateTime dateTime);

    List<Booking> findAllByItemInAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(List<Item> items,
                                                                                             LocalDateTime startDateTime,
                                                                                             LocalDateTime endDateTime);

    List<Booking> findAllByItemInAndStatusOrderByStartDesc(List<Item> items, BookingStatuses bookingStatus);

    Optional<Booking> findFirstByItem_IdAndStatusAndStartIsLessThanEqualOrderByStartDesc(Long itemId,
                                                                                         BookingStatuses status,
                                                                                         LocalDateTime dateTime);// last

    Optional<Booking> findFirstByItem_IdAndStatusAndStartIsGreaterThanEqualOrderByStartAsc(Long itemId,
                                                                                           BookingStatuses status,
                                                                                           LocalDateTime dateTime);

    List<Booking> findAllByItemInAndStatusAndStartIsLessThanEqualOrderByStartDesc(List<Item> items,
                                                                                  BookingStatuses status,
                                                                                  LocalDateTime dateTime);//last

    List<Booking> findAllByItemInAndStatusAndStartIsGreaterThanEqualOrderByStartAsc(List<Item> items,
                                                                                    BookingStatuses status,
                                                                                    LocalDateTime dateTime);

    Optional<Booking> findFirstByBooker_IdAndItem_IdAndEndBeforeOrderByStartDesc(Long bookerId, Long itemId,
                                                                                 LocalDateTime dateTime);
}
