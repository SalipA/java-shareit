package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ItemController.class)
@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemClient itemClient;
    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    public void shouldCreateItemNotValidRequestNameBlankCase() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto))
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Название вещи не может быть пустым"));
        Mockito.verify(itemClient, Mockito.never()).create(1L, itemDto);
    }

    @SneakyThrows
    @Test
    public void shouldCreateItemNotValidRequestDescriptionBlankCase() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto))
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Описание вещи не может быть пустым"));
        Mockito.verify(itemClient, Mockito.never()).create(1L, itemDto);
    }

    @SneakyThrows
    @Test
    public void shouldCreateItemNotValidRequestAvailableNullCase() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setRequestId(1L);

        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto))
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Статус доступности аренды должен быть указан"));
        Mockito.verify(itemClient, Mockito.never()).create(1L, itemDto);
    }
}
