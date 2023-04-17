package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exception.ItemEditAccessException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final UserDao userDao;
    private final ItemMapper itemMapper;
    private final ItemDao itemDao;

    public ItemServiceImpl(UserDao userDao, ItemMapper itemMapper, ItemDao itemDao) {
        this.userDao = userDao;
        this.itemMapper = itemMapper;
        this.itemDao = itemDao;
    }

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        userDao.checkUserId(userId);
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(userId);
        return itemMapper.toItemDto(itemDao.create(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        itemDao.checkItemId(itemId);
        Item item = itemMapper.toItem(itemDto);
        Item itemFromStorage = itemDao.read(itemId);
        if (itemFromStorage.getOwner().equals(userId)) {
            if (item.getName() == null) {
                item.setName(itemFromStorage.getName());
            }
            if (item.getDescription() == null) {
                item.setDescription(itemFromStorage.getDescription());
            }
            if (item.getAvailable() == null) {
                item.setAvailable(itemFromStorage.getAvailable());
            }
            item.setId(itemId);
            item.setOwner(userId);
        } else {
            throw new ItemEditAccessException(userId, itemId);
        }
        return itemMapper.toItemDto(itemDao.update(itemId, item));
    }

    @Override
    public ItemDto read(Long itemId) {
        itemDao.checkItemId(itemId);
        return itemMapper.toItemDto(itemDao.read(itemId));
    }

    @Override
    public List<ItemDto> readAllByUserId(Long userId) {
        userDao.checkUserId(userId);
        return itemMapper.listToItemDto(itemDao.readAllByUserId(userId));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            log.warn("search request was empty");
            return List.of();
        }
        if (getAvailable(itemDao.searchItems(text)).isEmpty()) {
            log.warn("no items found per search request");
            return List.of();
        } else {
            log.info(getAvailable(itemDao.searchItems(text)).toString());
            return itemMapper.listToItemDto(getAvailable(itemDao.searchItems(text)));
        }
    }

    private List<Item> getAvailable(List<Item> items) {
        List<Item> availableItems = new ArrayList<>();
        for (Item item : items) {
            if (item.getAvailable()) {
                availableItems.add(item);
            }
        }
        return availableItems;
    }
}