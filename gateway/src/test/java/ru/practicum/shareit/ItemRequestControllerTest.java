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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestDto;

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
    private ItemRequestClient itemRequestClient;

    @SneakyThrows
    @Test
    public void shouldCreateItemRequestNotValidRequestCase() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        mockMvc.perform(post("/requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemRequestDto))
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Описание запроса на может быть пустым"));
        Mockito.verify(itemRequestClient, Mockito.never()).create(1L, itemRequestDto);
    }

    @SneakyThrows
    @Test
    public void shouldReadAllItemRequestFromInvalidCase() {
        int from = -1;
        int size = 0;

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .get("/requests/all")
            .header("X-Sharer-User-Id", 1L)
            .param("from", Integer.toString(from))
            .param("size", Integer.toString(size));

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("{javax.validation.constraints.Min.message}"));
    }

    @SneakyThrows
    @Test
    public void shouldReadAllItemRequestSizeInvalidCase() {
        int from = 0;
        int size = 0;

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .get("/requests/all")
            .header("X-Sharer-User-Id", 1L)
            .param("from", Integer.toString(from))
            .param("size", Integer.toString(size));

        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("{javax.validation.constraints.Min.message}"));
    }
}
