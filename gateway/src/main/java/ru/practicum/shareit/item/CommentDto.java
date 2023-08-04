package ru.practicum.shareit.item;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Schema(description = "Комментарий")
public class CommentDto {
    @Schema(description = "id комментария", accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Long id;
    @Schema(description = "текст комметария", example = "Макбук бесподобный! Спасибо владельцу")
    @Parameter(required = true)
    @NotBlank(message = "Описание вещи не может быть пустым")
    @Size(max = 512, message = "Превышен лимит символов для поля text")
    private String text;
    @Schema(description = "имя автора", example = "Максим", accessMode = Schema.AccessMode.READ_ONLY)
    private String authorName;
    @Schema(description = "дата создания комментария", example = "2024-05-25T13:10:00", accessMode =
        Schema.AccessMode.READ_ONLY)
    private LocalDateTime created;
}
