package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.PaginationParamException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.ItemEditAccessException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapper bookingMapper;

    public BookingServiceImpl(BookingRepository bookingRepository, UserService userService, @Lazy ItemService
        itemService, BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemService = itemService;
        this.bookingMapper = bookingMapper;
    }

    @Transactional
    @Override
    public BookingDto create(Long userId, BookingDto bookingDto) {
        Booking booking = bookingMapper.toBooking(bookingDto);
        booking.setBooker(userService.checkUser(userId));
        booking.setItem(itemService.checkItemIsAvailableForBooking(userId, bookingDto.getItemId()));
        Booking bookingFromDataBase = bookingRepository.save(booking);
        log.info("Booking id = {} has been created", bookingFromDataBase.getId());
        return BookingMapper.toBookingDto(bookingFromDataBase);
    }

    @Override
    public BookingDto read(Long userId, Long bookingId) {
        userService.checkUser(userId);
        Optional<Booking> bookingFromDataBase = bookingRepository.findById(bookingId);
        if (bookingFromDataBase.isPresent()) {
            validateUserAccess(userId, bookingFromDataBase.get());
            log.info(bookingFromDataBase.get().toString());
            return BookingMapper.toBookingDto(bookingFromDataBase.get());
        } else {
            log.error("Booking id = {} is not found", bookingId);
            throw new BookingNotFoundException(bookingId);
        }
    }

    @Transactional
    @Override
    public BookingDto approveBooking(Long userId, Long bookingId, Boolean approved) {
        userService.checkUser(userId);
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
        return BookingMapper.toBookingDto(bookingFromDataBase.get());
    }

    @Override
    public List<BookingDto> getAllByState(Long userId, States state, Integer from, Integer size) {
        userService.checkUser(userId);
        Page<Booking> listBookingFromDataBase = null;
        Pageable pageRequest = setPageRequest(from, size);
        switch (state) {
            case ALL:
                listBookingFromDataBase = bookingRepository.findAllByBooker_IdOrderByStartDesc(userId,
                    pageRequest);
                break;
            case FUTURE:
                listBookingFromDataBase = bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(userId,
                    LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                listBookingFromDataBase = bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(userId,
                    LocalDateTime.now(), pageRequest);
                break;
            case CURRENT:
                LocalDateTime now = LocalDateTime.now();
                listBookingFromDataBase =
                    bookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now,
                        pageRequest);
                break;
            case WAITING:
                listBookingFromDataBase = bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId,
                    BookingStatuses.WAITING, pageRequest);
                break;
            case REJECTED:
                listBookingFromDataBase = bookingRepository.findAllByBooker_IdAndStatusOrderByStartDesc(userId,
                    BookingStatuses.REJECTED, pageRequest);
                break;
        }
        return listBookingFromDataBase.map(BookingMapper::toBookingDto).getContent();
    }

    @Override
    public List<BookingDto> getAllByOwnerAndState(Long userId, States state, Integer from, Integer size) {
        userService.checkUser(userId);
        Page<Booking> listBookingFromDataBase = null;
        Pageable pageRequest = setPageRequest(from, size);
        List<Item> itemFromDataBase = itemService.findItemsByOwner(userId);
            switch (state) {
                case ALL:
                    listBookingFromDataBase = bookingRepository.findAllByItemInOrderByStartDesc(itemFromDataBase, pageRequest);
                    break;
                case FUTURE:
                    listBookingFromDataBase =
                        bookingRepository.findAllByItemInAndStartGreaterThanEqualOrderByStartDesc(itemFromDataBase,
                            LocalDateTime.now(), pageRequest);
                    break;
                case PAST:
                    listBookingFromDataBase =
                        bookingRepository.findAllByItemInAndEndLessThanEqualOrderByStartDesc(itemFromDataBase,
                            LocalDateTime.now(), pageRequest);
                    break;
                case CURRENT:
                    LocalDateTime now = LocalDateTime.now();
                    listBookingFromDataBase =
                        bookingRepository.findAllByItemInAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(itemFromDataBase, now, now, pageRequest);
                    break;
                case WAITING:
                    listBookingFromDataBase =
                        bookingRepository.findAllByItemInAndStatusOrderByStartDesc(itemFromDataBase,
                            BookingStatuses.WAITING, pageRequest);
                    break;
                case REJECTED:
                    listBookingFromDataBase =
                        bookingRepository.findAllByItemInAndStatusOrderByStartDesc(itemFromDataBase,
                            BookingStatuses.REJECTED, pageRequest);
                    break;
            }
        return listBookingFromDataBase.map(BookingMapper::toBookingDto).getContent();
    }

    @Override
    public Optional<Booking> findNextBookingForItem(Long itemId, BookingStatuses status, LocalDateTime dateTime) {
        return bookingRepository.findFirstByItem_IdAndStatusAndStartIsGreaterThanEqualOrderByStartAsc(itemId,
            status, dateTime);
    }

    @Override
    public Optional<Booking> findLastBookingForItem(Long itemId, BookingStatuses status, LocalDateTime dateTime) {
        return bookingRepository.findFirstByItem_IdAndStatusAndStartIsLessThanEqualOrderByStartDesc(itemId,
            status, dateTime);
    }

    @Override
    public List<Booking> findAllNextBookingForItems(List<Item> items, BookingStatuses status, LocalDateTime dateTime) {
        return bookingRepository.findAllByItemInAndStatusAndStartIsGreaterThanEqualOrderByStartAsc(items,
            status, dateTime);
    }

    @Override
    public List<Booking> findAllLastBookingForItems(List<Item> items, BookingStatuses status, LocalDateTime dateTime) {
        return bookingRepository.findAllByItemInAndStatusAndStartIsLessThanEqualOrderByStartDesc(items,
            status, dateTime);
    }

    @Override
    public Optional<Booking> findEndedBookingForItemByUser(Long userId, Long itemId, LocalDateTime dateTime) {
        return bookingRepository.findFirstByBooker_IdAndItem_IdAndEndBeforeOrderByStartDesc(userId, itemId, dateTime);
    }

    private void validateUserAccess(Long userId, Booking booking) {
        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().equals(userId))) {
            log.error("User id = {} has not access to read info booking id = {}", userId, booking.getId());
            throw new BookingReadAccessException(userId, booking.getId());
        }
    }

    private Pageable setPageRequest(Integer from, Integer size) {
        if (from == null && size == null) {
            return Pageable.unpaged();
        } else if (from == null || size == null) {
            log.error("Pagination parameters from = {}, size = {} are not allowed", from, size);
            throw new PaginationParamException(from, size);
        } else {
            return PageRequest.of(from > 0 ? from / size : 0, size);
        }
    }
}