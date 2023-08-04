package ru.practicum.shareit.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Предзаказ на добавление вещи")
public class ItemRequestDto {
    @Schema(description = "id предзаказа", accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Long id;
    @Schema(description = "описание предзаказа", example = "с удовольствием взял бы макбук")
    @Parameter(required = true)
    @NotBlank(message = "Описание запроса на может быть пустым")
    private String description;
    @Schema(description = "дата создания запроса", accessMode = Schema.AccessMode.READ_ONLY, example = "2024-05-25T13:10:00")
    private LocalDateTime created;
}
