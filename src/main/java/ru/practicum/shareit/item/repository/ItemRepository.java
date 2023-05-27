package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(" select i from Item i " +
        "where upper(i.name) like upper(concat('%', ?1, '%')) " +
        "   or upper(i.description) like upper(concat('%', ?1, '%')) and i.available = true")
    Page<Item> searchItem(String text, Pageable pageable);

    Page<Item> findItemsByOwnerOrderByIdAsc(Long userId, Pageable pageable);
}
