package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatuses;
import ru.practicum.shareit.item.dto.ItemView;
import ru.practicum.shareit.user.dto.UserView;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class BookingDto {
    private Long id;
    @NotNull(message = "ItemId должен быть указан")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long itemId;
    @FutureOrPresent(message = "Дата начала аренды должна быть текущей или в будущем")
    @NotNull(message = "Дата начала аренды должна быть указана")
    private LocalDateTime start;
    @FutureOrPresent(message = "Дата окончания аренды должна быть в будущем")
    @NotNull(message = "Дата окончания аренды должна быть указана")
    private LocalDateTime end;
    private ItemView item;
    private UserView booker;
    private BookingStatuses status;

    public BookingDto(Long itemId, LocalDateTime start, LocalDateTime end) {
        this.itemId = itemId;
        this.start = start;
        this.end = end;
    }
}
