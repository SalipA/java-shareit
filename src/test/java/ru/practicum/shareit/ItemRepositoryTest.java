package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository repository;

    @Test
    public void shouldSearchItemStandardCase() {
        User user = new User();
        user.setName("user");
        user.setEmail("user@mail.ru");

        Item item = new Item();
        item.setName("компьютер новый");
        item.setDescription("большой");
        item.setAvailable(true);
        item.setOwner(1L);

        Item item2 = new Item();
        item2.setName("компьютерная мышь");
        item2.setDescription("новая беспроводная");
        item2.setAvailable(true);
        item2.setOwner(1L);

        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(item2);

        List<Item> items = List.of(item, item2);
        Page<Item> itemPage = new PageImpl<>(items);

        Page<Item> actual = repository.searchItem("комп", Pageable.unpaged());

        Assertions.assertEquals(itemPage, actual);
    }
}
