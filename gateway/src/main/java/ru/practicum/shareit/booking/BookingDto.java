package ru.practicum.shareit.booking;

import lombok.*;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    @NotNull(message = "ItemId должен быть указан")
    private Long itemId;
    @FutureOrPresent(message = "Дата начала аренды должна быть текущей или в будущем")
    @NotNull(message = "Дата начала аренды должна быть указана")
    private LocalDateTime start;
    @FutureOrPresent(message = "Дата окончания аренды должна быть в будущем")
    @NotNull(message = "Дата окончания аренды должна быть указана")
    private LocalDateTime end;
}