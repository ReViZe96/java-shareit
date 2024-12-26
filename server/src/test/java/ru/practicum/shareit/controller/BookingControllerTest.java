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
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingFilter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController controller;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private MockMvc mvc;

    private BookingResponseDto bookingResponseDto;
    private BookingRequestDto bookingRequestDto;
    private ItemDto itemDto;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("First Item");
        itemDto.setDescription("first item description");
        itemDto.setAvailable(true);

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("First User");
        userDto.setEmail("john.doe@mail.com");

        bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(1L);
        bookingResponseDto.setStatus(BookingStatus.WAITING);
        bookingResponseDto.setStart(LocalDateTime.of(2025, 10, 9, 8, 7));
        bookingResponseDto.setEnd(LocalDateTime.of(2025, 11, 10, 9, 8));
        bookingResponseDto.setItem(itemDto);
        bookingResponseDto.setBooker(userDto);


        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(1L);
        bookingRequestDto.setStart(LocalDateTime.of(2025, 10, 9, 8, 7));
        bookingRequestDto.setEnd(LocalDateTime.of(2025, 11, 10, 9, 8));


    }

    @Test
    public void shouldGetAllUserBookings() throws Exception {
        when(bookingService.getAllUserBookings(1L, BookingFilter.WAITING))
                .thenReturn(List.of(bookingResponseDto));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("existFilter", BookingFilter.WAITING.name())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is(bookingResponseDto.getStatus().name())))
                .andExpect(jsonPath("$.[0].start.[1]", is(bookingResponseDto.getStart().getMonthValue()), LocalDateTime.class))
                .andExpect(jsonPath("$.[0].end.[1]", is(bookingResponseDto.getEnd().getMonthValue()), LocalDateTime.class))
                .andExpect(jsonPath("$.[0].item", is(bookingResponseDto.getItem()), ItemDto.class))
                .andExpect(jsonPath("$.[0].booker", is(bookingResponseDto.getBooker()), UserDto.class));
    }

    @Test
    public void shouldGetAllItemBookings() throws Exception {
        when(bookingService.getAllItemBookings(1L, BookingFilter.WAITING))
                .thenReturn(List.of(bookingResponseDto));
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("existFilter", BookingFilter.WAITING.name())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is(bookingResponseDto.getStatus().name())))
                .andExpect(jsonPath("$.[0].start.[1]", is(bookingResponseDto.getStart().getMonthValue()), LocalDateTime.class))
                .andExpect(jsonPath("$.[0].end.[1]", is(bookingResponseDto.getEnd().getMonthValue()), LocalDateTime.class))
                .andExpect(jsonPath("$.[0].item", is(bookingResponseDto.getItem()), ItemDto.class))
                .andExpect(jsonPath("$.[0].booker", is(bookingResponseDto.getBooker()), UserDto.class));
    }

    @Test
    public void shouldGetBookingById() throws Exception {
        when(bookingService.getBookingById(userDto.getId(), bookingResponseDto.getId()))
                .thenReturn(bookingResponseDto);
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("bookingId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().name())))
                .andExpect(jsonPath("$.start.[1]", is(bookingResponseDto.getStart().getMonthValue()), LocalDateTime.class))
                .andExpect(jsonPath("$.end.[1]", is(bookingResponseDto.getEnd().getMonthValue()), LocalDateTime.class))
                .andExpect(jsonPath("$.item", is(bookingResponseDto.getItem()), ItemDto.class))
                .andExpect(jsonPath("$.booker", is(bookingResponseDto.getBooker()), UserDto.class));
    }

    @Test
    public void shouldAddBooking() throws Exception {
        when(bookingService.addBooking(1L, bookingRequestDto))
                .thenReturn(bookingResponseDto);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().name())))
                .andExpect(jsonPath("$.start.[1]", is(bookingResponseDto.getStart().getMonthValue()), LocalDateTime.class))
                .andExpect(jsonPath("$.end.[1]", is(bookingResponseDto.getEnd().getMonthValue()), LocalDateTime.class))
                .andExpect(jsonPath("$.item", is(bookingResponseDto.getItem()), ItemDto.class))
                .andExpect(jsonPath("$.booker", is(bookingResponseDto.getBooker()), UserDto.class));
    }

    @Test
    public void shouldApproveBooking() throws Exception {
        when(bookingService.approveBooking(1L, 1L, true))
                .thenReturn(bookingResponseDto);
        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().name())))
                .andExpect(jsonPath("$.start.[1]", is(bookingResponseDto.getStart().getMonthValue()), LocalDateTime.class))
                .andExpect(jsonPath("$.end.[1]", is(bookingResponseDto.getEnd().getMonthValue()), LocalDateTime.class))
                .andExpect(jsonPath("$.item", is(bookingResponseDto.getItem()), ItemDto.class))
                .andExpect(jsonPath("$.booker", is(bookingResponseDto.getBooker()), UserDto.class));
    }

}
