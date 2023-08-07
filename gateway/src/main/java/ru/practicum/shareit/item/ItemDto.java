package ru.practicum.shareit.item;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Вещь")
public class ItemDto {
    @Schema(description = "id вещи", accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Long id;
    @Schema(description = "название вещи", example = "ноутбук")
    @Parameter(required = true)
    @NotBlank(message = "название вещи не может быть пустым")
    private String name;
    @Schema(description = "описание вещи", example = "Apple MacBook Air 2023 pink gold")
    @Parameter(required = true)
    @NotBlank(message = "Описание вещи не может быть пустым")
    private String description;
    @Schema(description = "доступность аренды", example = "true")
    @Parameter(required = true)
    @NotNull(message = "Статус доступности аренды должен быть указан")
    private Boolean available;
    @Schema(description = "id предзаказа на вещь", example = "1")
    private Long requestId;
}