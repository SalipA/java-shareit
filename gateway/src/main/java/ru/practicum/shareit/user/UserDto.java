package ru.practicum.shareit.user;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Пользователь")
public class UserDto {
    @Schema(description = "id пользователя", accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Long id;
    @Schema(description = "имя пользователя", example = "Иван")
    @Parameter(required = true)
    @NotBlank(message = "Имя не может быть пустым значением")
    private String name;
    @Schema(description = "email пользователя", example = "ivan@mail.ru")
    @Parameter(required = true)
    @NotBlank(message = "Email не может быть пустым значением")
    @Email(message = "Email должен иметь формат адреса электронной почты")
    private String email;
}