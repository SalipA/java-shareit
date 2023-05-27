package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Booking b set b.status = :status where b.id = :id")
    void updateBookingStatus(@Param(value = "id") Long id, @Param(value = "status") BookingStatuses bookingStatuses);

    Page<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime dateTime, Pageable
        pageable);

    Page<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime dateTime, Pageable
        pageable);

    Page<Booking> findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId,
                                                                              LocalDateTime startDateTime,
                                                                              LocalDateTime endDateTime, Pageable
                                                                                  pageable);

    Page<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatuses bookingStatus, Pageable
        pageable);

    Page<Booking> findAllByItemInOrderByStartDesc(List<Item> items, Pageable pageable);

    Page<Booking> findAllByItemInAndStartGreaterThanEqualOrderByStartDesc(List<Item> items, LocalDateTime dateTime,
                                                                          Pageable pageable);

    Page<Booking> findAllByItemInAndEndLessThanEqualOrderByStartDesc(List<Item> items, LocalDateTime dateTime,
                                                                     Pageable pageable);

    Page<Booking> findAllByItemInAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(List<Item> items,
                                                                                             LocalDateTime startDateTime,
                                                                                             LocalDateTime endDateTime,
                                                                                             Pageable pageable);

    Page<Booking> findAllByItemInAndStatusOrderByStartDesc(List<Item> items, BookingStatuses bookingStatus,
                                                           Pageable pageable);

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
