package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.ItemBookingAccessException;
import ru.practicum.shareit.item.exception.ItemEditAccessException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository
        itemRepository, BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingMapper = bookingMapper;
    }

    @Transactional
    @Override
    public BookingDto create(Long userId, BookingDto bookingDto) {
        Booking booking = bookingMapper.toBooking(bookingDto);
        booking.setBooker(checkUser(userId));
        booking.setItem(checkItem(userId, bookingDto.getItemId()));
        validateStartAndEndDate(bookingDto.getStart(), bookingDto.getEnd());
        Booking bookingFromDataBase = bookingRepository.save(booking);
        log.info("Booking id = {} has been created", bookingFromDataBase.getId());
        return bookingMapper.toBookingDto(bookingFromDataBase);
    }

    @Override
    public BookingDto read(Long userId, Long bookingId) {
        checkUser(userId);
        Optional<Booking> bookingFromDataBase = bookingRepository.findById(bookingId);
        if (bookingFromDataBase.isPresent()) {
            validateUserAccess(userId, bookingFromDataBase.get());
            log.info(bookingFromDataBase.get().toString());
            return bookingMapper.toBookingDto(bookingFromDataBase.get());
        } else {
            log.error("Booking id = {} is not found", bookingId);
            throw new BookingNotFoundException(bookingId);
        }
    }

    @Transactional
    @Override
    public BookingDto approveBooking(Long userId, Long bookingId, Boolean approved) {
        checkUser(userId);
        Optional<Booking> bookingFromDataBase = bookingRepository.findById(bookingId);
        if (bookingFromDataBase.isPresent()) {
            if (!Objects.equals(bookingFromDataBase.get().getItem().getOwner(), userId)) {
                log.error("User id = {} has no access to edit Booking id = {}", userId, bookingId);
                throw new ItemEditAccessException(userId, bookingFromDataBase.get().getItem().getId());
            }
            if (bookingFromDataBase.get().getStatus().equals(BookingStatuses.WAITING)) {
                if (approved) {
                    bookingRepository.updateBookingStatus(bookingId, BookingStatuses.APPROVED);
                    bookingFromDataBase.get().setStatus(BookingStatuses.APPROVED);
                    log.info("Booking id = {} status was changed to APPROVED", bookingId);
                } else {
                    bookingRepository.updateBookingStatus(bookingId, BookingStatuses.REJECTED);
                    bookingFromDataBase.get().setStatus(BookingStatuses.REJECTED);
                    log.info("Booking id = {} status was changed to REJECTED", bookingId);
                }
            } else {
                log.error("Booking id = {} status has already been changed", bookingId);
                throw new BookingStatusAlreadyChangedException(bookingId);
            }
        } else {
            log.error("Booking id = {} is not found", bookingId);
            throw new BookingNotFoundException(bookingId);
        }
        return bookingMapper.toBookingDto(bookingFromDataBase.get());
    }

    @Override
    public List<BookingDto> getAllByState(Long userId, States state) {
        checkUser(userId);
        List<Booking> listBookingFromDataBase = new ArrayList<>();
        switch (state) {
            case ALL:
                listBookingFromDataBase = bookingRepository.findAllByBooker_IdOrderByStartDesc(userId);
                break;
            case FUTURE:
                listBookingFromDataBase = bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case PAST:
                listBookingFromDataBase = bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case CURRENT:
                LocalDateTime now = LocalDateTime.now();
                listBookingFromDataBase = bookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                break;
            case WAITING:
                listBookingFromDataBase = bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatuses.WAITING);
                break;
            case REJECTED:
                listBookingFromDataBase = bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatuses.REJECTED);
                break;
        }
        return bookingMapper.listToBookingDto(listBookingFromDataBase);
    }

    @Override
    public List<BookingDto> getAllByOwnerAndState(Long userId, States state) {
        checkUser(userId);
        List<Booking> listBookingFromDataBase = new ArrayList<>();
        List<Item> itemFromDataBase = itemRepository.findItemsByOwnerOrderByIdAsc(userId);
        if (!itemFromDataBase.isEmpty()) {
            switch (state) {
                case ALL:
                    listBookingFromDataBase = bookingRepository.findAllByItemInOrderByStartDesc(itemFromDataBase);
                    break;
                case FUTURE:
                    listBookingFromDataBase = bookingRepository.findAllByItemInAndStartGreaterThanEqualOrderByStartDesc(itemFromDataBase, LocalDateTime.now());
                    break;
                case PAST:
                    listBookingFromDataBase = bookingRepository.findAllByItemInAndEndLessThanEqualOrderByStartDesc(itemFromDataBase, LocalDateTime.now());
                    break;
                case CURRENT:
                    LocalDateTime now = LocalDateTime.now();
                    listBookingFromDataBase = bookingRepository.findAllByItemInAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(itemFromDataBase, now, now);
                    break;
                case WAITING:
                    listBookingFromDataBase = bookingRepository.findAllByItemInAndStatusOrderByStartDesc(itemFromDataBase, BookingStatuses.WAITING);
                    break;
                case REJECTED:
                    listBookingFromDataBase = bookingRepository.findAllByItemInAndStatusOrderByStartDesc(itemFromDataBase, BookingStatuses.REJECTED);
                    break;
            }
        } else {
            log.error("User id = {} is not items owner", userId);
            throw new ItemNotFoundException(itemFromDataBase, userId);
        }
        return bookingMapper.listToBookingDto(listBookingFromDataBase);
    }

    private Item checkItem(Long userId, Long itemId) {
        Optional<Item> itemFromDataBase = itemRepository.findById(itemId);
        if (itemFromDataBase.isPresent()) {
            if (!itemFromDataBase.get().getOwner().equals(userId)) {
                if (itemFromDataBase.get().getAvailable()) {
                    return itemFromDataBase.get();
                } else {
                    log.error("Item id = {} is not available to sharing", itemId);
                    throw new ItemBookingAccessException(itemId);
                }
            } else {
                log.error("User id = {} can not book Item id = {} because is item owner", userId, itemId);
                throw new BookingOwnerCreateException(userId, itemId);
            }
        } else {
            log.error("Item id = {} is not found", itemId);
            throw new ItemNotFoundException(itemId);
        }
    }

    private User checkUser(Long userId) {
        Optional<User> userFromDataBase = userRepository.findById(userId);
        if (userFromDataBase.isPresent()) {
            return userFromDataBase.get();
        } else {
            log.error("User id = {} is not found", userId);
            throw new UserNotFoundException(userId);
        }
    }

    private void validateStartAndEndDate(LocalDateTime start, LocalDateTime end) {
        if (start.equals(end)) {
            log.error("End date must be after Start date");
            throw new BookingStartEndTimeValidationException("Дата окончания бронирования не может быть датой начала " +
                "бронирования");
        } else if (start.isAfter(end)) {
            log.error("End date must be after Start date");
            throw new BookingStartEndTimeValidationException("Дата окончания бронирования не может быть ранее даты " +
                "начала бронирования");
        }
    }

    private void validateUserAccess(Long userId, Booking booking) {
        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().equals(userId))) {
            log.error("User id = {} has not access to read info booking id = {}", userId, booking.getId());
            throw new BookingReadAccessException(userId, booking.getId());
        }
    }
}