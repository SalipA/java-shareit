package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserService userService;

    private final Clock clock;


    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
                                  ItemRequestMapper itemRequestMapper, UserService userService, Clock clock) {
        this.itemRequestRepository = itemRequestRepository;
        this.itemRequestMapper = itemRequestMapper;
        this.userService = userService;
        this.clock = clock;
    }

    @Transactional
    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        LocalDateTime now = LocalDateTime.now(clock);
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(userService.checkUser(userId));
        itemRequest.setCreated(now);
        ItemRequest itemRequestFromDataBase = itemRequestRepository.save(itemRequest);
        log.info("Request id = {} has been created", itemRequestFromDataBase.getId());
        return ItemRequestMapper.toItemRequestDto(itemRequestFromDataBase);
    }

    @Override
    public ItemRequestDto read(Long userId, Long requestId) {
        userService.checkUser(userId);
        Optional<ItemRequest> itemRequestFromDataBase = itemRequestRepository.findById(requestId);
        if (itemRequestFromDataBase.isPresent()) {
            return ItemRequestMapper.toItemRequestDto(itemRequestFromDataBase.get());
        } else {
            log.error("Request id = {} is not found", requestId);
            throw new RequestNotFoundException(requestId);
        }
    }

    @Override
    public List<ItemRequestDto> getAllByUserId(Long userId) {
        User userFromDataBase = userService.checkUser(userId);
        List<ItemRequest> itemRequestsFromDataBase =
            itemRequestRepository.findAllByRequestorOrderByCreatedDesc(userFromDataBase);
        if (itemRequestsFromDataBase.isEmpty()) {
            return List.of();
        } else {
            return itemRequestMapper.listToItemRequestDto(itemRequestsFromDataBase);
        }
    }

    @Override
    public List<ItemRequestDto> getAllRequestsWithPagination(Long userId, Integer from, Integer size) {
        userService.checkUser(userId);
        Pageable pageRequest;
        if (from == null) {
            pageRequest = Pageable.unpaged();
        } else {
            pageRequest = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(Sort.Order.desc("created")));
        }
        Page<ItemRequest> page = itemRequestRepository.findAllByRequestor_IdIsNot(userId, pageRequest);
        return page.map(ItemRequestMapper::toItemRequestDto).getContent();
    }
}
