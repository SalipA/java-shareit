package ru.practicum.shareit.booking;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Заявка на бронирование вещи")
public class BookingDto {
    @Schema(description = "id бронирования", accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Long id;
    @Schema(description = "id бронируемой вещи", example = "1")
    @Parameter(required = true)
    @NotNull(message = "ItemId должен быть указан")
    private Long itemId;
    @Schema(description = "Желаемая дата начала аренды", example = "2024-05-23T13:10:00")
    @Parameter(required = true)
    @FutureOrPresent(message = "Дата начала аренды должна быть текущей или в будущем")
    @NotNull(message = "Дата начала аренды должна быть указана")
    private LocalDateTime start;
    @Schema(description = "Желаемая дата окончания аренды", example = "2024-05-25T13:10:00")
    @FutureOrPresent(message = "Дата окончания аренды должна быть в будущем")
    @NotNull(message = "Дата окончания аренды должна быть указана")
    private LocalDateTime end;
}