package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.exception.BookingOwnerCreateException;
import ru.practicum.shareit.booking.model.Booking;
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
import ru.practicum.shareit.item.service.CommentMapper;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    UserService userService;
    @Mock
    ItemMapper itemMapper;
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingService bookingService;
    @Mock
    CommentRepository commentRepository;
    @Mock
    CommentMapper commentMapper;
    @InjectMocks
    ItemServiceImpl itemService;
    @Captor
    ArgumentCaptor<Item> itemArgumentCaptor;

    @Test
    public void shouldCreateItemStandardCase() {
        ItemDto expected = new ItemDto();
        expected.setName("name");
        expected.setDescription("description");
        expected.setAvailable(true);
        expected.setRequestId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setRequest(1L);

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(itemMapper.toItem(expected)).thenReturn(item);
        Mockito.when(itemMapper.toItemDto(item)).thenReturn(expected);
        Mockito.when(itemRepository.save(item)).thenReturn(item);

        ItemDto actual = itemService.create(0L, expected);

        Assertions.assertEquals(actual, expected);
        Mockito.verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();
        Assertions.assertEquals(1L, savedItem.getId());
        Assertions.assertEquals("name", savedItem.getName());
        Assertions.assertEquals("description", savedItem.getDescription());
        Assertions.assertTrue(savedItem.getAvailable());
        Assertions.assertEquals(1L, savedItem.getRequest());
    }

    @Test
    public void shouldUpdateItemUserNotFoundCase() {
        ItemDto expected = new ItemDto();
        Item item = new Item();

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenThrow(UserNotFoundException.class);

        Assertions.assertThrows(UserNotFoundException.class,
            () -> itemService.update(0L, 0L, expected));
        Mockito.verify(itemRepository, Mockito.never()).save(item);
    }

    @Test
    public void shouldUpdateItemItemNotFoundCase() {
        ItemDto expected = new ItemDto();
        Item item = new Item();

        Mockito.when(itemMapper.toItem(expected)).thenReturn(item);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenThrow(new ItemNotFoundException(0L));

        Assertions.assertThrows(ItemNotFoundException.class,
            () -> itemService.update(0L, 0L, expected));
        Mockito.verify(itemRepository, Mockito.never()).save(item);
    }

    @Test
    public void shouldUpdateItemUserNotOwnerCase() {
        ItemDto expected = new ItemDto();
        Item item = new Item();
        item.setOwner(1L);

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(itemMapper.toItem(expected)).thenReturn(item);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        Assertions.assertThrows(ItemEditAccessException.class,
            () -> itemService.update(0L, 0L, expected));
        Mockito.verify(itemRepository, Mockito.never()).save(item);
    }

    @Test
    public void shouldUpdateItemUserOwnerNewNameCase() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("new name");

        Item oldItem = new Item();
        oldItem.setId(1L);
        oldItem.setName("name");
        oldItem.setDescription("description");
        oldItem.setAvailable(true);
        oldItem.setRequest(1L);
        oldItem.setOwner(1L);

        Item newItem = new Item();
        newItem.setName("new name");

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(itemMapper.toItem(itemDto)).thenReturn(newItem);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(oldItem));
        Mockito.when(itemRepository.save(newItem)).thenReturn(newItem);
        Mockito.when(itemMapper.toItemDto(newItem)).thenReturn(itemDto);

        itemService.update(1L, 1L, itemDto);

        Mockito.verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();

        Assertions.assertEquals(1L, savedItem.getId());
        Assertions.assertEquals("new name", savedItem.getName());
        Assertions.assertEquals("description", savedItem.getDescription());
        Assertions.assertTrue(savedItem.getAvailable());
        Assertions.assertEquals(1L, savedItem.getRequest());
    }

    @Test
    public void shouldUpdateItemUserOwnerNewDescriptionCase() {
        ItemDto itemDto = new ItemDto();
        itemDto.setDescription("new description");

        Item oldItem = new Item();
        oldItem.setId(1L);
        oldItem.setName("name");
        oldItem.setDescription("description");
        oldItem.setAvailable(true);
        oldItem.setRequest(1L);
        oldItem.setOwner(1L);

        Item newItem = new Item();
        newItem.setDescription("new description");

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(itemMapper.toItem(itemDto)).thenReturn(newItem);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(oldItem));
        Mockito.when(itemRepository.save(newItem)).thenReturn(newItem);
        Mockito.when(itemMapper.toItemDto(newItem)).thenReturn(itemDto);

        itemService.update(1L, 1L, itemDto);

        Mockito.verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();

        Assertions.assertEquals(1L, savedItem.getId());
        Assertions.assertEquals("name", savedItem.getName());
        Assertions.assertEquals("new description", savedItem.getDescription());
        Assertions.assertTrue(savedItem.getAvailable());
        Assertions.assertEquals(1L, savedItem.getRequest());
    }

    @Test
    public void shouldUpdateItemUserOwnerNewAvailableCase() {
        ItemDto itemDto = new ItemDto();
        itemDto.setAvailable(false);

        Item oldItem = new Item();
        oldItem.setId(1L);
        oldItem.setName("name");
        oldItem.setDescription("description");
        oldItem.setAvailable(true);
        oldItem.setRequest(1L);
        oldItem.setOwner(1L);

        Item newItem = new Item();
        newItem.setAvailable(false);

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(itemMapper.toItem(itemDto)).thenReturn(newItem);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(oldItem));
        Mockito.when(itemRepository.save(newItem)).thenReturn(newItem);
        Mockito.when(itemMapper.toItemDto(newItem)).thenReturn(itemDto);

        itemService.update(1L, 1L, itemDto);

        Mockito.verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();

        Assertions.assertEquals(1L, savedItem.getId());
        Assertions.assertEquals("name", savedItem.getName());
        Assertions.assertEquals("description", savedItem.getDescription());
        Assertions.assertFalse(savedItem.getAvailable());
        Assertions.assertEquals(1L, savedItem.getRequest());
    }

    @Test
    public void shouldUpdateItemUserOwnerNewRequestCase() {
        ItemDto itemDto = new ItemDto();
        itemDto.setRequestId(3L);

        Item oldItem = new Item();
        oldItem.setId(1L);
        oldItem.setName("name");
        oldItem.setDescription("description");
        oldItem.setAvailable(true);
        oldItem.setRequest(1L);
        oldItem.setOwner(1L);

        Item newItem = new Item();
        newItem.setRequest(3L);

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(itemMapper.toItem(itemDto)).thenReturn(newItem);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(oldItem));
        Mockito.when(itemRepository.save(newItem)).thenReturn(newItem);
        Mockito.when(itemMapper.toItemDto(newItem)).thenReturn(itemDto);

        itemService.update(1L, 1L, itemDto);

        Mockito.verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();

        Assertions.assertEquals(1L, savedItem.getId());
        Assertions.assertEquals("name", savedItem.getName());
        Assertions.assertEquals("description", savedItem.getDescription());
        Assertions.assertTrue(savedItem.getAvailable());
        Assertions.assertEquals(3L, savedItem.getRequest());
    }

    @Test
    public void shouldReadItemUserNotOwnerCase() {
        Item oldItem = new Item();
        oldItem.setId(1L);
        oldItem.setName("name");
        oldItem.setDescription("description");
        oldItem.setAvailable(true);
        oldItem.setRequest(1L);
        oldItem.setOwner(1L);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        List<Comment> comments = List.of(new Comment());
        List<CommentDto> commentDtos = List.of(new CommentDto());
        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(oldItem));
        Mockito.when(itemMapper.toItemDto(Mockito.any())).thenReturn(itemDto);
        Mockito.when(commentRepository.findAllByItem_IdOrderByIdDesc(Mockito.anyLong())).thenReturn(comments);
        Mockito.when(commentMapper.listToCommentDto(Mockito.anyList())).thenReturn(commentDtos);

        ItemDto actual = itemService.read(1L, 2L);

        Assertions.assertNull(actual.getNextBooking());
        Assertions.assertNull(actual.getLastBooking());
        Assertions.assertEquals(commentDtos, actual.getComments());
    }

    @Test
    public void shouldReadItemUserOwnerCase() {
        Item oldItem = new Item();
        oldItem.setId(1L);
        oldItem.setName("name");
        oldItem.setDescription("description");
        oldItem.setAvailable(true);
        oldItem.setRequest(1L);
        oldItem.setOwner(1L);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        User user = new User();
        user.setId(3L);

        Booking next = new Booking();
        next.setId(1L);
        next.setBooker(user);
        next.setStart(LocalDateTime.of(2023, 8, 8, 8, 8, 8));
        next.setEnd(LocalDateTime.of(2023, 8, 8, 8, 8, 8));

        Booking last = new Booking();
        last.setId(2L);
        last.setBooker(user);
        last.setStart(LocalDateTime.of(2023, 1, 1, 1, 1, 1));
        last.setEnd(LocalDateTime.of(2023, 2, 2, 2, 2, 2));

        List<Comment> comments = List.of(new Comment());
        List<CommentDto> commentDtos = List.of(new CommentDto());

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(oldItem));
        Mockito.when(itemMapper.toItemDto(Mockito.any())).thenReturn(itemDto);
        Mockito.when(bookingService.findNextBookingForItem(Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(Optional.of(next));
        Mockito.when(bookingService.findLastBookingForItem(Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(Optional.of(last));
        Mockito.when(commentRepository.findAllByItem_IdOrderByIdDesc(Mockito.anyLong())).thenReturn(comments);
        Mockito.when(commentMapper.listToCommentDto(Mockito.anyList())).thenReturn(commentDtos);

        ItemDto actual = itemService.read(1L, 1L);

        Assertions.assertEquals(actual.getNextBooking().getId(), next.getId());
        Assertions.assertEquals(actual.getNextBooking().getStart(), next.getStart());
        Assertions.assertEquals(actual.getNextBooking().getEnd(), next.getEnd());

        Assertions.assertEquals(actual.getLastBooking().getId(), last.getId());
        Assertions.assertEquals(actual.getLastBooking().getStart(), last.getStart());
        Assertions.assertEquals(actual.getLastBooking().getEnd(), last.getEnd());

        Assertions.assertEquals(commentDtos, actual.getComments());
    }

    @Test
    public void shouldCreateCommentNoBookingCase() {
        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Item()));
        Mockito.when(bookingService.findEndedBookingForItemByUser(Mockito.anyLong(), Mockito.anyLong(),
            Mockito.any())).thenThrow(new CommentAddAccessException(0L, 0L));

        Assertions.assertThrows(CommentAddAccessException.class, () -> itemService.createComment(0L, 0L,
            new CommentDto()));
        Mockito.verify(commentRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void shouldCreateCommentStandardCase() {
        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Item()));
        Mockito.when(bookingService.findEndedBookingForItemByUser(Mockito.anyLong(), Mockito.anyLong(),
            Mockito.any())).thenReturn(Optional.of(new Booking()));
        Mockito.when(commentMapper.toComment(Mockito.any())).thenReturn(new Comment());
        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(new Comment());
        Mockito.when(commentMapper.toCommentDto(Mockito.any())).thenReturn(new CommentDto());

        itemService.createComment(0L, 0L, new CommentDto());

        Mockito.verify(commentRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    public void shouldSearchItemTextEmptyCase() {
        Assertions.assertEquals(itemService.searchItems("", null, null), List.of());
    }

    @Test
    public void shouldSearchItemsFrom6Size2Case() {
        List<ItemDto> expected = List.of();
        Mockito.when(itemRepository.searchItem(Mockito.any(), Mockito.any())).thenReturn(Page.empty());
        Mockito.when(itemMapper.listToItemDto(Mockito.anyList())).thenReturn(expected);

        List<ItemDto> actual = itemService.searchItems("text", 6, 2);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void shouldReadAllByUserIdStandardCase() {
        Item oldItem = new Item();
        oldItem.setId(1L);
        oldItem.setName("name");
        oldItem.setDescription("description");
        oldItem.setAvailable(true);
        oldItem.setRequest(1L);
        oldItem.setOwner(1L);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        User user = new User();
        user.setId(3L);

        Booking next = new Booking();
        next.setId(1L);
        next.setBooker(user);
        next.setStart(LocalDateTime.of(2023, 8, 8, 8, 8, 8));
        next.setEnd(LocalDateTime.of(2023, 8, 8, 8, 8, 8));
        next.setItem(oldItem);

        Booking last = new Booking();
        last.setId(2L);
        last.setBooker(user);
        last.setStart(LocalDateTime.of(2023, 1, 1, 1, 1, 1));
        last.setEnd(LocalDateTime.of(2023, 2, 2, 2, 2, 2));
        last.setItem(oldItem);

        Comment comment = new Comment();
        comment.setItem(oldItem);

        CommentDto commentDto = new CommentDto();

        List<Comment> comments = List.of(comment);
        List<CommentDto> commentDtos = List.of(new CommentDto());

        List<Item> items = List.of(oldItem);
        Page<Item> pagedResponse = new PageImpl<>(items);

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(itemRepository.findItemsByOwnerOrderByIdAsc(Mockito.anyLong(), Mockito.any())).thenReturn(pagedResponse);
        Mockito.when(bookingService.findAllNextBookingForItems(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(List.of(next));
        Mockito.when(bookingService.findAllLastBookingForItems(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(List.of(last));
        Mockito.when(itemMapper.listToItemDto(Mockito.anyList())).thenReturn(List.of(itemDto));
        Mockito.when(commentRepository.findAllByItemInOrderByIdDesc(Mockito.any())).thenReturn(comments);
        Mockito.when(commentMapper.toCommentDto(Mockito.any())).thenReturn(commentDto);

        List<ItemDto> actual = itemService.readAllByUserId(1L, null, null);

        Assertions.assertEquals(actual.get(0).getNextBooking().getId(), next.getId());
        Assertions.assertEquals(actual.get(0).getNextBooking().getStart(), next.getStart());
        Assertions.assertEquals(actual.get(0).getNextBooking().getEnd(), next.getEnd());

        Assertions.assertEquals(actual.get(0).getLastBooking().getId(), last.getId());
        Assertions.assertEquals(actual.get(0).getLastBooking().getStart(), last.getStart());
        Assertions.assertEquals(actual.get(0).getLastBooking().getEnd(), last.getEnd());

        Assertions.assertEquals(commentDtos, actual.get(0).getComments());

        Assertions.assertEquals(1, actual.size());
    }

    @Test
    public void shouldCheckItemIsAvailableFalseForBooking() {
        Item oldItem = new Item();
        oldItem.setId(1L);
        oldItem.setName("name");
        oldItem.setDescription("description");
        oldItem.setAvailable(false);
        oldItem.setRequest(1L);
        oldItem.setOwner(1L);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(oldItem));
        Assertions.assertThrows(ItemBookingAccessException.class,
            () -> itemService.checkItemIsAvailableForBooking(2L, 1L));

    }

    @Test
    public void shouldCheckItemIsAvailableForBookingOwnerCase() {
        Item oldItem = new Item();
        oldItem.setId(1L);
        oldItem.setName("name");
        oldItem.setDescription("description");
        oldItem.setAvailable(true);
        oldItem.setRequest(1L);
        oldItem.setOwner(1L);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(oldItem));
        Assertions.assertThrows(BookingOwnerCreateException.class,
            () -> itemService.checkItemIsAvailableForBooking(1L, 1L));
    }

    @Test
    public void shouldCheckBookingCase() {
        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(new User());
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Item()));
        Mockito.when(bookingService.findEndedBookingForItemByUser(Mockito.anyLong(), Mockito.anyLong(),
            Mockito.any())).thenReturn(Optional.empty());
        Assertions.assertThrows(CommentAddAccessException.class, () -> itemService.createComment(1L,
            1L, new CommentDto()));
    }
}