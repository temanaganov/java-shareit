package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.core.exception.ExceptionsHandler;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .setControllerAdvice(ExceptionsHandler.class)
                .build();
    }

    @Test
    void getAllByBooker() throws Exception {
        long bookerId = 1;

        Mockito.when(bookingService.getAllByBooker(Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings").header(USER_ID_HEADER, bookerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.emptyList())));
    }

    @Test
    void getAllByOwner() throws Exception {
        long bookerId = 1;

        Mockito.when(bookingService.getAllByOwner(Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/bookings/owner").header(USER_ID_HEADER, bookerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.emptyList())));
    }

    @Test
    void getById() throws Exception {
        long bookingId = 1;
        long bookerId = 1;
        Booking booking = new Booking(bookingId, null, null, null, null, null);

        Mockito.when(bookingService.getById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(booking);

        mockMvc.perform(get("/bookings/" + bookingId).header(USER_ID_HEADER, bookerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(booking)));
    }

    @Test
    void create() throws Exception {
        long bookingId = 1;
        long bookerId = 1;
        Booking booking = new Booking(bookingId, null, null, null, null, null);
        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now());
        String json = objectMapper.writeValueAsString(bookingDto);

        Mockito.when(bookingService.create(Mockito.anyLong(), Mockito.any())).thenReturn(booking);

        mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON).header(USER_ID_HEADER, bookerId).content(json))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(booking)));
    }

    @Test
    void create_badRequest() throws Exception {
        long bookerId = 1;
        BookingDto bookingDto = new BookingDto(1L, null, null);
        String json = objectMapper.writeValueAsString(bookingDto);

        mockMvc.perform(post("/bookings").contentType(MediaType.APPLICATION_JSON).header(USER_ID_HEADER, bookerId).content(json))
                .andExpect(status().isBadRequest());
    }


    @Test
    void update() throws Exception {
        long bookingId = 1;
        long bookerId = 1;
        Booking booking = new Booking(bookingId, null, null, null, null, null);

        Mockito.when(bookingService.update(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(booking);

        mockMvc.perform(patch("/bookings/" + bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, bookerId)
                        .queryParam("approved", "true")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(booking)));
    }
}
