package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым значением")
    private String name;
    @NotBlank(message = "Email не может быть пустым значением")
    @Email(message = "Email должен иметь формат адреса электронной почты")
    private String email;
}