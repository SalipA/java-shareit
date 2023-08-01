package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class CommentDto {
    private Long id;
    @NotBlank(message = "Описание вещи не может быть пустым")
    @Size(max = 512, message = "Превышен лимит символов для поля text")
    private String text;
    private String authorName;
    private LocalDateTime created;
}
