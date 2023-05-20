package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class ItemRequestDto {
    private Long id;
    @NotBlank(message = "Описание запроса на может быть пустым")
    private String description;
    private LocalDateTime created;
    private List<Item> items;

    public ItemRequestDto(Long id, String description, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.created = created;
    }
}
