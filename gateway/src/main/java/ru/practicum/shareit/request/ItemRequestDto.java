package ru.practicum.shareit.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    @NotBlank(message = "Описание запроса на может быть пустым")
    private String description;
    private LocalDateTime created;
}
