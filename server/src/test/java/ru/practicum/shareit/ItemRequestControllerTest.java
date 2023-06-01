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
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ItemRequestController.class)
@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService itemRequestService;

    @SneakyThrows
    @Test
    public void shouldCreateStandardCase() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("description");

        Mockito.when(itemRequestService.create(1L, itemRequestDto)).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemRequestDto))
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.description").value("description"))
            .andExpect(jsonPath("$.created").hasJsonPath())
            .andExpect(jsonPath("$.items").hasJsonPath());

        Mockito.verify(itemRequestService, Mockito.times(1)).create(1L, itemRequestDto);
    }

    @SneakyThrows
    @Test
    public void shouldReadUItemRequestStandardCase() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("description");

        Mockito.when(itemRequestService.read(1L, 1L)).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.description").value("description"))
            .andExpect(jsonPath("$.created").hasJsonPath())
            .andExpect(jsonPath("$.items").hasJsonPath());

        Mockito.verify(itemRequestService, Mockito.times(1)).read(1L, 1L);
    }

    @SneakyThrows
    @Test
    public void shouldReadItemRequestNotFoundCase() {

        Mockito.when(itemRequestService.read(1L, 1L)).thenThrow(new RequestNotFoundException(1L));

        mockMvc.perform(get("/requests/{requestId}", 1L)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Запрос вещи с requestId = 1 не найден!"));

        Mockito.verify(itemRequestService, Mockito.times(1)).read(1L, 1L);
    }

    @SneakyThrows
    @Test
    public void shouldReadItemRequestNoHeaderCase() {

        mockMvc.perform(get("/requests/{requestId}", 1L))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("userId владельца вещи должен быть указан"));
    }

    @SneakyThrows
    @Test
    public void shouldGetAllByUserIdStandardCase() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("desc");

        ItemRequestDto itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setId(2L);
        itemRequestDto2.setDescription("desc2");

        List<ItemRequestDto> itemRequestDtoList = List.of(itemRequestDto, itemRequestDto2);

        Mockito.when(itemRequestService.getAllByUserId(1L)).thenReturn(itemRequestDtoList);

        mockMvc.perform(get("/requests")
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value("1"))
            .andExpect(jsonPath("$[0].description").value("desc"))
            .andExpect(jsonPath("$[1].id").value("2"))
            .andExpect(jsonPath("$[1].description").value("desc2"));


        Mockito.verify(itemRequestService, Mockito.times(1)).getAllByUserId(1L);
    }

    @SneakyThrows
    @Test
    public void shouldGetAllStandardCase() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("desc");

        ItemRequestDto itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setId(2L);
        itemRequestDto2.setDescription("desc2");

        List<ItemRequestDto> itemRequestDtoList = List.of(itemRequestDto, itemRequestDto2);

        Mockito.when(itemRequestService.getAllRequestsWithPagination(1L, 0, 1)).thenReturn(itemRequestDtoList);

        mockMvc.perform(get("/requests/all")
                .header("X-Sharer-User-Id", 1)
                .param("from", "0")
                .param("size", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value("1"))
            .andExpect(jsonPath("$[0].description").value("desc"))
            .andExpect(jsonPath("$[1].id").value("2"))
            .andExpect(jsonPath("$[1].description").value("desc2"));


        Mockito.verify(itemRequestService, Mockito.times(1))
            .getAllRequestsWithPagination(1L, 0, 1);
    }
}