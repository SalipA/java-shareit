package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Slf4j
@Repository
public class ItemDaoImpl implements ItemDao {
    private final Map<Long, Item> idItems = new HashMap<>();
    private final Map<Long, List<Long>> userIdItemsId = new HashMap<>();
    private final Map<Long, String> itemIdName = new HashMap<>();
    private final Map<Long, String> itemIdDescription = new HashMap<>();
    private Long idCounter = 0L;

    @Override
    public Item create(Item item) {
        idCounter++;
        item.setId(idCounter);
        idItems.put(idCounter, item);
        addItemToUserItems(item);
        addItemToItemName(item);
        addItemToItemDescription(item);
        log.info("Item id = {} has been created", item.getId());
        return item;
    }

    @Override
    public Item update(Long itemId, Item item) {
        idItems.remove(itemId);
        idItems.put(itemId, item);
        addItemToItemName(item);
        addItemToItemDescription(item);
        log.info("Item id = {} has been updated", item.getId());
        return item;
    }

    @Override
    public Item read(Long itemId) {
        log.info(idItems.get(itemId).toString());
        return idItems.get(itemId);
    }

    @Override
    public List<Item> readAllByUserId(Long userId) {
        List<Long> itemsId = userIdItemsId.get(userId);
        List<Item> items = new ArrayList<>();
        for (Long itemId : itemsId) {
            items.add(idItems.get(itemId));
        }
        log.info(items.toString());
        return items;
    }

    @Override
    public List<Item> searchItems(String text) {
        Map<Long, Item> items = new TreeMap<>();
        for (Long itemId : itemIdName.keySet()) {
            if (StringUtils.containsIgnoreCase(itemIdName.get(itemId), text)) {
                items.put(itemId, idItems.get(itemId));
            }
        }
        for (Long itemId : itemIdDescription.keySet()) {
            if (StringUtils.containsIgnoreCase(itemIdDescription.get(itemId), text)) {
                items.put(itemId, idItems.get(itemId));
            }
        }
        return new ArrayList<>(items.values());
    }

    private void addItemToUserItems(Item item) {
        if (userIdItemsId.containsKey(item.getOwner())) {
            List<Long> itemsIdList = userIdItemsId.get(item.getOwner());
            if (!itemsIdList.contains(item.getId())) {
                itemsIdList.add(item.getId());
                userIdItemsId.remove(item.getOwner());
                userIdItemsId.put(item.getOwner(), itemsIdList);
            }
        } else {
            checkItemId(item.getId());
            List<Long> itemsIdList = new ArrayList<>();
            itemsIdList.add(item.getId());
            userIdItemsId.put(item.getOwner(), itemsIdList);
        }
    }

    private void addItemToItemName(Item item) {
        if (itemIdName.containsKey(item.getId())) {
            itemIdName.remove(item.getId());
            itemIdName.put(item.getId(), item.getName());
        } else {
            itemIdName.put(item.getId(), item.getName());
        }
    }

    private void addItemToItemDescription(Item item) {
        if (itemIdDescription.containsKey(item.getId())) {
            itemIdDescription.remove(item.getId());
            itemIdDescription.put(item.getId(), item.getDescription());
        } else {
            itemIdDescription.put(item.getId(), item.getDescription());
        }
    }

    @Override
    public void checkItemId(Long itemId) {
        if (!idItems.containsKey(itemId)) {
            log.error("Item id = {} is not found", itemId);
            throw new ItemNotFoundException(itemId);
        }
    }
}