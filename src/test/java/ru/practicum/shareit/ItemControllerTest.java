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
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ItemController.class)
@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    public void shouldCreateStandardCase() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        Mockito.when(itemService.create(1L, itemDto)).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto))
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.name").value("name"))
            .andExpect(jsonPath("$.description").value("description"))
            .andExpect(jsonPath("$.available").value("true"))
            .andExpect(jsonPath("$.lastBooking").hasJsonPath())
            .andExpect(jsonPath("$.nextBooking").hasJsonPath())
            .andExpect(jsonPath("$.comments").hasJsonPath())
            .andExpect(jsonPath("$.requestId").value("1"));

        Mockito.verify(itemService, Mockito.times(1)).create(1L, itemDto);
    }

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
        Mockito.verify(itemService, Mockito.never()).create(1L, itemDto);
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
        Mockito.verify(itemService, Mockito.never()).create(1L, itemDto);
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
        Mockito.verify(itemService, Mockito.never()).create(1L, itemDto);
    }

    @SneakyThrows
    @Test
    public void shouldUpdateStandardCase() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        Mockito.when(itemService.update(1L, 1L, itemDto)).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto))
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.name").value("name"))
            .andExpect(jsonPath("$.description").value("description"))
            .andExpect(jsonPath("$.available").value("true"))
            .andExpect(jsonPath("$.lastBooking").hasJsonPath())
            .andExpect(jsonPath("$.nextBooking").hasJsonPath())
            .andExpect(jsonPath("$.comments").hasJsonPath())
            .andExpect(jsonPath("$.requestId").value("1"));

        Mockito.verify(itemService, Mockito.times(1)).update(1L, 1L, itemDto);
    }

    @SneakyThrows
    @Test
    public void shouldReadStandardCase() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        Mockito.when(itemService.read(1L, 1L)).thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.name").value("name"))
            .andExpect(jsonPath("$.description").value("description"))
            .andExpect(jsonPath("$.available").value("true"))
            .andExpect(jsonPath("$.lastBooking").hasJsonPath())
            .andExpect(jsonPath("$.nextBooking").hasJsonPath())
            .andExpect(jsonPath("$.comments").hasJsonPath())
            .andExpect(jsonPath("$.requestId").value("1"));

        Mockito.verify(itemService, Mockito.times(1)).read(1L, 1L);
    }

    @SneakyThrows
    @Test
    public void shouldReadAllByUserIdStandardCase() {
        int from = 0;
        int size = 1;

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        List<ItemDto> itemDtoList = List.of(itemDto);

        Mockito.when(itemService.readAllByUserId(1L, from, size)).thenReturn(itemDtoList);

        mockMvc.perform(get("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)
                .param("from", Integer.toString(from))
                .param("size", Integer.toString(size)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value("1"))
            .andExpect(jsonPath("$[0].name").value("name"))
            .andExpect(jsonPath("$[0].description").value("description"))
            .andExpect(jsonPath("$[0].available").value("true"))
            .andExpect(jsonPath("$[0].lastBooking").hasJsonPath())
            .andExpect(jsonPath("$[0].nextBooking").hasJsonPath())
            .andExpect(jsonPath("$[0].comments").hasJsonPath())
            .andExpect(jsonPath("$[0].requestId").value("1"));

        Mockito.verify(itemService, Mockito.times(1)).readAllByUserId(1L, from, size);
    }

    @SneakyThrows
    @Test
    public void shouldSearchStandardCase() {
        int from = 0;
        int size = 1;

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        List<ItemDto> itemDtoList = List.of(itemDto);

        Mockito.when(itemService.searchItems("name", from, size)).thenReturn(itemDtoList);

        mockMvc.perform(get("/items/search")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)
                .param("text", "name")
                .param("from", Integer.toString(from))
                .param("size", Integer.toString(size)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value("1"))
            .andExpect(jsonPath("$[0].name").value("name"))
            .andExpect(jsonPath("$[0].description").value("description"))
            .andExpect(jsonPath("$[0].available").value("true"))
            .andExpect(jsonPath("$[0].lastBooking").hasJsonPath())
            .andExpect(jsonPath("$[0].nextBooking").hasJsonPath())
            .andExpect(jsonPath("$[0].comments").hasJsonPath())
            .andExpect(jsonPath("$[0].requestId").value("1"));

        Mockito.verify(itemService, Mockito.times(1)).searchItems("name", from, size);
    }

    @SneakyThrows
    @Test
    public void shouldCreateCommentStandardCase() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment");

        Mockito.when(itemService.createComment(1L, 1L, commentDto)).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto))
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.text").value("comment"));


        Mockito.verify(itemService, Mockito.times(1)).createComment(1L, 1L, commentDto);
    }
}
