package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dao.ItemDaoImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dao.UserDaoImpl;
import ru.practicum.shareit.user.model.User;

import java.util.LinkedList;
import java.util.List;

public class ItemServiceTests {
    ItemService itemService;
    ItemDto standardCaseItemDto;
    UserDaoImpl userDao;

    @BeforeEach
    public void createItemService() {
        userDao = new UserDaoImpl();
        itemService = new ItemServiceImpl(userDao, new ItemMapper(), new ItemDaoImpl());
    }

    @BeforeEach
    public void createUserDtoStandardCase() {
        User firstUser = userDao.create(new User());
        firstUser.setName("testName");
        firstUser.setEmail("test@test.ru");
        userDao.update(1L, firstUser);
    }

    @BeforeEach
    public void createItemDtoStandardCase() {
        standardCaseItemDto = new ItemDto(0L, "тестовая вещь", "большая", true);
    }

    @Test
    public void shouldCreateItemStandardCase() {
        ItemDto itemDto = itemService.create(1L, standardCaseItemDto);
        standardCaseItemDto.setId(1L);
        Assertions.assertEquals(itemDto, standardCaseItemDto);
    }

    @Test
    public void shouldUpdateItemStandardCase() {
        itemService.create(1L, standardCaseItemDto);
        ItemDto itemNew = new ItemDto(1L, "тестовая вещь измененная", "большая имененная",
            false);
        itemService.update(1L, 1L, itemNew);
        Assertions.assertEquals(itemService.read(1L).getName(), "тестовая вещь измененная");
        Assertions.assertEquals(itemService.read(1L).getDescription(), "большая имененная");
        Assertions.assertEquals(itemService.read(1L).getAvailable(), false);
        Assertions.assertEquals(itemService.readAllByUserId(1L).size(), 1);
    }

    @Test
    public void shouldReadItemStandardCase() {
        itemService.create(1L, standardCaseItemDto);
        Assertions.assertEquals(itemService.read(1L).getName(), "тестовая вещь");
        Assertions.assertEquals(itemService.read(1L).getDescription(), "большая");
        Assertions.assertEquals(itemService.read(1L).getAvailable(), true);
    }

    @Test
    public void shouldReadAllItemByUserIdStandardCase() {
        ItemDto itemNew = new ItemDto(2L, "тестовая вещь измененная", "большая имененная",
            false);
        itemService.create(1L, standardCaseItemDto);
        itemService.create(1L, itemNew);
        List<ItemDto> userItemList = new LinkedList<>();
        standardCaseItemDto.setId(1L);
        userItemList.add(standardCaseItemDto);
        userItemList.add(itemNew);
        Assertions.assertEquals(itemService.readAllByUserId(1L), userItemList);
    }

    @Test
    public void shouldSearchItemStandardCase() {
        String text = "большая";
        itemService.create(1L, standardCaseItemDto);
        ItemDto item2 = new ItemDto(2L, "тестовая вещь измененная", "измененная",
            false);
        ItemDto item3 = new ItemDto(3L, "вещь большая", "измененная",
            true);
        itemService.create(1L, item2);
        itemService.create(1L, item3);
        List<ItemDto> userItemList = new LinkedList<>();
        standardCaseItemDto.setId(1L);
        userItemList.add(standardCaseItemDto);
        userItemList.add(item3);
        Assertions.assertEquals(itemService.searchItems(text), userItemList);
    }
}