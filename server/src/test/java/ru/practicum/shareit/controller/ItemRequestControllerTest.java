package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.AvailableItemDto;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {

    @Mock
    private ItemRequestService requestService;

    @InjectMocks
    private ItemRequestController controller;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private MockMvc mvc;

    private ItemRequestResponseDto responseDto;
    private ItemRequestDto requestDto;
    private AvailableItemDto itemDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        itemDto = new AvailableItemDto();
        itemDto.setId(1L);
        itemDto.setName("First Item");
        itemDto.setOwnerId(1L);

        responseDto = new ItemRequestResponseDto();
        responseDto.setId(1L);
        responseDto.setDescription("first item request");
        responseDto.setCreated(true);
        responseDto.setItems(List.of(itemDto));

        requestDto = new ItemRequestDto();
        requestDto.setDescription("first item request");
    }


    @Test
    public void shouldGetAllAnotherUserRequests() throws Exception {
        when(requestService.getAllAnotherUserRequests(2L))
                .thenReturn(List.of(responseDto));
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(responseDto.getDescription())))
                .andExpect(jsonPath("$.[0].created", is(responseDto.getCreated())))
                .andExpect(jsonPath("$.[0].items.[0]", is(responseDto.getItems().get(0)), AvailableItemDto.class));
    }

    @Test
    public void shouldGetOnlyThisUserRequests() throws Exception {
        when(requestService.getOnlyThisUserRequests(1L))
                .thenReturn(List.of(responseDto));
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(responseDto.getDescription())))
                .andExpect(jsonPath("$.[0].created", is(responseDto.getCreated())))
                .andExpect(jsonPath("$.[0].items.[0]", is(responseDto.getItems().get(0)), AvailableItemDto.class));
    }

    @Test
    public void shouldGetRequestById() throws Exception {
        when(requestService.getRequestById(1L))
                .thenReturn(responseDto);
        mvc.perform(get("/requests/1")
                        .param("itemId", itemDto.getId().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(responseDto.getDescription())))
                .andExpect(jsonPath("$.created", is(responseDto.getCreated())))
                .andExpect(jsonPath("$.items.[0]", is(responseDto.getItems().get(0)), AvailableItemDto.class));
    }

    @Test
    public void shouldAddRequest() throws Exception {
        when(requestService.addRequest(requestDto, 1L))
                .thenReturn(responseDto);
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(responseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(responseDto.getDescription())))
                .andExpect(jsonPath("$.created", is(responseDto.getCreated())))
                .andExpect(jsonPath("$.items.[0]", is(responseDto.getItems().get(0)), AvailableItemDto.class));
    }

}
