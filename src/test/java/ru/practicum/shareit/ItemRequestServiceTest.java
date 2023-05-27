package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserService userService;

    ItemRequestServiceImpl itemRequestService;

    @Captor
    ArgumentCaptor<ItemRequest> itemRequestArgumentCaptor;

    @Mock
    Clock clock;

    @BeforeEach
    public void createServiceAndMocks() {
        this.itemRequestService = new ItemRequestServiceImpl(itemRequestRepository,
            new ItemRequestMapper(), userService, clock);
    }

    @Test
    public void shouldCreateItemRequestStandardCase() {

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("description");
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("description");
        User requestor = new User();
        itemRequest.setRequestor(requestor);

        LocalDateTime fake = LocalDateTime.of(2023, 1, 1, 1, 1, 1);
        Mockito.when(clock.instant()).thenReturn(fake.toInstant(ZoneOffset.UTC));
        Mockito.when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        //Mockito.when(itemRequestMapper.toItemRequest(itemRequestDto)).thenReturn(itemRequest);
        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(requestor);
        Mockito.when(itemRequestRepository.save(Mockito.any())).thenReturn(itemRequest);

        itemRequestService.create(1L, itemRequestDto);

        Mockito.verify(itemRequestRepository).save(itemRequestArgumentCaptor.capture());
        ItemRequest savedItemRequest = itemRequestArgumentCaptor.getValue();
        //Assertions.assertEquals(1L, savedItemRequest.getId());
        Assertions.assertEquals("description", savedItemRequest.getDescription());
        Assertions.assertEquals(fake, savedItemRequest.getCreated());
    }

    @Test
    public void shouldReadItemRequestStandardCase() {
        Long id = 2L;
        ItemRequestDto expected = new ItemRequestDto();
        expected.setItems(List.of());
        ItemRequest itemRequest = new ItemRequest();
        User requestor = new User();

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(requestor);
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(itemRequest));

        ItemRequestDto actual = itemRequestService.read(id, id);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void shouldReadItemRequestStandardCase2() {

        User requestor = new User();

        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(requestor);
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(RequestNotFoundException.class,
            () -> itemRequestService.read(1L, 1L));
    }

    @Test
    public void shouldReadItemRequestNotFoundCase() {
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong())).thenThrow(RequestNotFoundException.class);

        Assertions.assertThrows(RequestNotFoundException.class,
            () -> itemRequestService.read(1L, 1L));
    }

    @Test
    public void shouldGetAllByUserIdStandardCase() {
        List<ItemRequestDto> expected = List.of(new ItemRequestDto());
        User requestor = new User();
        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(requestor);
        Mockito.when(itemRequestRepository.findAllByRequestorOrderByCreatedDesc(requestor)).thenReturn(List.of(new ItemRequest()));
        //Mockito.when(itemRequestMapper.listToItemRequestDto(Mockito.anyList())).thenReturn(expected);
        List<ItemRequestDto> actual = itemRequestService.getAllByUserId(0L);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetAllByUserIdEmptyItemRequestCase() {
        List<ItemRequestDto> expected = List.of(new ItemRequestDto());
        User requestor = new User();
        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(requestor);
        Mockito.when(itemRequestRepository.findAllByRequestorOrderByCreatedDesc(requestor)).thenReturn(List.of(new ItemRequest()));
        //Mockito.when(itemRequestMapper.listToItemRequestDto(Mockito.anyList())).thenReturn(expected);
        List<ItemRequestDto> actual = itemRequestService.getAllByUserId(0L);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetAllByUserIdEmptyItemRequestCase2() {
        List<ItemRequestDto> expected = List.of();
        User requestor = new User();
        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(requestor);
        Mockito.when(itemRequestRepository.findAllByRequestorOrderByCreatedDesc(requestor)).thenReturn(List.of());
        List<ItemRequestDto> actual = itemRequestService.getAllByUserId(0L);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetAllRequestsWithPaginationStandardCase() {
        List<ItemRequestDto> expected = List.of();
        User requestor = new User();
        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(requestor);
        Mockito.when(itemRequestRepository.findAllByRequestor_IdIsNot(0L, Pageable.unpaged())).thenReturn(Page.empty());
        List<ItemRequestDto> actual = itemRequestService.getAllRequestsWithPagination(0L, null, null);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetAllRequestsWithPaginationFrom6Size2() {
        List<ItemRequestDto> expected = List.of();
        User requestor = new User();
        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(requestor);
        Mockito.when(itemRequestRepository.findAllByRequestor_IdIsNot(0L, PageRequest.of(3, 2,
            Sort.by(Sort.Order.desc("created"))))).thenReturn(Page.empty());
        List<ItemRequestDto> actual = itemRequestService.getAllRequestsWithPagination(0L, 6, 2);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetAllRequestsWithPaginationFrom0Size2() {
        List<ItemRequestDto> expected = List.of();
        User requestor = new User();
        Mockito.when(userService.checkUser(Mockito.anyLong())).thenReturn(requestor);
        Mockito.when(itemRequestRepository.findAllByRequestor_IdIsNot(0L, PageRequest.of(0, 2,
            Sort.by(Sort.Order.desc("created"))))).thenReturn(Page.empty());
        List<ItemRequestDto> actual = itemRequestService.getAllRequestsWithPagination(0L, 0, 2);
        Assertions.assertEquals(expected, actual);
    }
}
