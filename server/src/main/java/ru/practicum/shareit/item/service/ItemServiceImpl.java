package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingView;
import ru.practicum.shareit.booking.exception.BookingOwnerCreateException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.CommentAddAccessException;
import ru.practicum.shareit.item.exception.ItemBookingAccessException;
import ru.practicum.shareit.item.exception.ItemEditAccessException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final BookingService bookingService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public ItemServiceImpl(UserService userService, ItemMapper itemMapper, ItemRepository itemRepository,
                           BookingService bookingService, CommentRepository commentRepository, CommentMapper
                               commentMapper) {
        this.userService = userService;
        this.itemMapper = itemMapper;
        this.itemRepository = itemRepository;
        this.bookingService = bookingService;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    @Transactional
    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        userService.checkUser(userId);
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userId);
        item.setAvailable(true);
        Item itemFromDataBase = itemRepository.save(item);
        log.info("Item id = {} has been created", itemFromDataBase.getId());
        return itemMapper.toItemDto(itemFromDataBase);
    }

    @Transactional
    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        userService.checkUser(userId);
        Item item = itemMapper.toItem(itemDto);
        Item itemFromDataBase = checkItem(itemId);
        if (itemFromDataBase.getOwner().equals(userId)) {
            if (item.getName() == null) {
                item.setName(itemFromDataBase.getName());
            }
            if (item.getDescription() == null) {
                item.setDescription(itemFromDataBase.getDescription());
            }
            if (item.getAvailable() == null) {
                item.setAvailable(itemFromDataBase.getAvailable());
            }
            if (item.getRequest() == null) {
                item.setRequest(itemFromDataBase.getRequest());
            }
            item.setId(itemId);
            item.setOwner(userId);
        } else {
            log.error("User id = {} has no access to edit Item id = {}", userId, itemId);
            throw new ItemEditAccessException(userId, itemId);
        }
        log.info("Item id = {} has been updated", itemId);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto read(Long itemId, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        userService.checkUser(userId);
        Item itemFromDataBase = checkItem(itemId);
        ItemDto itemDto = itemMapper.toItemDto(itemFromDataBase);
        if (isUserItemOwner(userId, itemId)) {
            Optional<Booking> bookingNextOpt = bookingService.findNextBookingForItem(itemId, BookingStatuses.APPROVED,
                now);
            bookingNextOpt.ifPresent(booking -> itemDto.setNextBooking(new BookingView(booking.getId(),
                booking.getBooker().getId(), booking.getStart(), booking.getEnd())));
            Optional<Booking> bookingLastOpt = bookingService.findLastBookingForItem(itemId, BookingStatuses.APPROVED,
                now);
            bookingLastOpt.ifPresent(booking -> itemDto.setLastBooking(new BookingView(booking.getId(),
                booking.getBooker().getId(), booking.getStart(), booking.getEnd())));
        }
        List<CommentDto> commentsFromDataBase =
            commentMapper.listToCommentDto(commentRepository.findAllByItem_IdOrderByIdDesc(itemId));
        itemDto.setComments(commentsFromDataBase);
        return itemDto;
    }

    @Override
    public List<ItemDto> readAllByUserId(Long userId, Integer from, Integer size) {
        LocalDateTime now = LocalDateTime.now();
        userService.checkUser(userId);
        Pageable pageRequest = setPageRequest(from, size);
        Page<Item> pageItems = itemRepository.findItemsByOwnerOrderByIdAsc(userId, pageRequest);
        List<Item> items = pageItems.getContent();
        List<Booking> bookingsLast = bookingService.findAllLastBookingForItems(items, BookingStatuses.APPROVED, now);
        List<Booking> bookingsNext = bookingService.findAllNextBookingForItems(items, BookingStatuses.APPROVED, now);
        List<ItemDto> itemsDto = itemMapper.listToItemDto(items);
        for (ItemDto item : itemsDto) {
            for (Booking booking : bookingsLast) {
                if (item.getId().equals(booking.getItem().getId())) {
                    item.setLastBooking(new BookingView(booking.getId(), booking.getBooker().getId(),
                        booking.getStart(), booking.getEnd()));
                    break;
                }
            }
        }
        for (ItemDto item : itemsDto) {
            for (Booking booking : bookingsNext) {
                if (item.getId().equals(booking.getItem().getId())) {
                    item.setNextBooking(new BookingView(booking.getId(), booking.getBooker().getId(),
                        booking.getStart(), booking.getEnd()));
                    break;
                }
            }
        }
        List<Comment> comments = commentRepository.findAllByItemInOrderByIdDesc(items);
        for (ItemDto item : itemsDto) {
            List<CommentDto> commentDtoList = new ArrayList<>();
            for (Comment comment : comments) {
                if (item.getId().equals(comment.getItem().getId())) {
                    CommentDto commentDto = commentMapper.toCommentDto(comment);
                    commentDtoList.add(commentDto);
                }
            }
            item.setComments(commentDtoList);
        }
        return itemsDto;
    }

    @Override
    public List<ItemDto> searchItems(String text, Integer from, Integer size) {
        Pageable pageRequest = setPageRequest(from, size);
        if (text.isEmpty()) {
            log.warn("search request was empty");
            return List.of();
        }
        return itemMapper.listToItemDto(itemRepository.searchItem(text, pageRequest).getContent());
    }

    @Transactional
    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        User userFromDataBase = userService.checkUser(userId);
        Item itemFromDataBase = checkItem(itemId);
        LocalDateTime now = LocalDateTime.now();
        checkBooking(userId, itemId, now);
        Comment comment = commentMapper.toComment(commentDto);
        comment.setItem(itemFromDataBase);
        comment.setAuthor(userFromDataBase);
        comment.setCreated(now);
        Comment commentFromDataBase = commentRepository.save(comment);
        log.info("Comment id = {} has been created", commentFromDataBase.getId());
        return commentMapper.toCommentDto(commentFromDataBase);
    }

    @Override
    public Item checkItem(Long itemId) {
        Optional<Item> itemFromDataBase = itemRepository.findById(itemId);
        if (itemFromDataBase.isPresent()) {
            return itemFromDataBase.get();
        } else {
            log.error("Item id = {} is not found", itemId);
            throw new ItemNotFoundException(itemId);
        }
    }

    @Override
    public List<Item> findItemsByOwner(Long userId) {
        List<Item> itemFromDataBase = itemRepository.findItemsByOwnerOrderByIdAsc(userId,
            Pageable.unpaged()).getContent();
        if (itemFromDataBase.isEmpty()) {
            log.error("User id = {} is not items owner", userId);
            throw new ItemNotFoundException(itemFromDataBase, userId);
        } else {
            return itemFromDataBase;
        }
    }

    @Override
    public Item checkItemIsAvailableForBooking(Long userId, Long itemId) {
        Item itemFromDataBase = checkItem(itemId);
        if (!itemFromDataBase.getOwner().equals(userId)) {
            if (itemFromDataBase.getAvailable()) {
                return itemFromDataBase;
            } else {
                log.error("Item id = {} is not available to sharing", itemId);
                throw new ItemBookingAccessException(itemId);
            }
        } else {
            log.error("User id = {} can not book Item id = {} because is item owner", userId, itemId);
            throw new BookingOwnerCreateException(userId, itemId);
        }
    }

    private void checkBooking(Long userId, Long itemId, LocalDateTime dateTime) {
        Optional<Booking> bookingFromDataBase = bookingService.findEndedBookingForItemByUser(userId, itemId, dateTime);
        if (bookingFromDataBase.isEmpty()) {
            log.error("User id = {} has no access to add comment to Item id = {}", userId, itemId);
            throw new CommentAddAccessException(userId, itemId);
        }
    }

    private Boolean isUserItemOwner(Long userId, Long itemId) {
        return checkItem(itemId).getOwner().equals(userId);
    }

    private Pageable setPageRequest(Integer from, Integer size) {
        if (from == null) {
            return Pageable.unpaged();
        } else {
            return PageRequest.of(from > 0 ? from / size : 0, size);
        }
    }
}