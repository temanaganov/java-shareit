package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.core.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.utils.TestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Spy
    private RequestMapper requestMapper = Mappers.getMapper(RequestMapper.class);

    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @InjectMocks
    private RequestService requestService;

    @Test
    void createRequest_shouldCreateNewRequest() {
        long userId = 1;
        User user = TestUtils.makeUser(userId);
        CreateRequestDto createRequestDto = new CreateRequestDto("New request");

        when(userService.getById(userId)).thenReturn(user);
        when(requestRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        RequestDto requestDto = requestService.createRequest(createRequestDto, userId);

        assertThat(requestDto.getDescription()).isEqualTo(createRequestDto.getDescription());
        assertThat(requestDto.getCreated()).isBefore(LocalDateTime.now());
        assertThat(requestDto.getItems()).isNull();
    }

    @Test
    void createRequest_shouldThrowNotFoundExceptionIfUserIsNotExists() {
        long userId = 1;
        CreateRequestDto createRequestDto = new CreateRequestDto("New request");

        when(userService.getById(userId)).thenThrow(NotFoundException.class);

        assertThatThrownBy(() -> {
            requestService.createRequest(createRequestDto, userId);
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getOwnRequests_shouldReturnListOfRequests() {
        long userId = 1;
        User user = TestUtils.makeUser(userId);
        List<Request> requests = List.of(
                TestUtils.makeRequest(1, LocalDateTime.now(), user),
                TestUtils.makeRequest(2, LocalDateTime.now(), user),
                TestUtils.makeRequest(3, LocalDateTime.now(), user)
        );

        when(userService.getById(userId)).thenReturn(user);
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(Collections.emptyList());
        when(requestRepository.findAllByUserIdOrderByCreatedDesc(anyLong())).thenReturn(requests);

        assertThat(requestService.getOwnRequests(userId)).isEqualTo(requests
                .stream()
                .map(requestMapper::requestToRequestDto)
                .map(requestDto -> {
                    requestDto.setItems(Collections.emptyList());
                    return requestDto;
                }).collect(Collectors.toList()));
    }

    @Test
    void getOtherRequests_shouldReturnListOfRequests() {
        long userId = 1;
        User user = TestUtils.makeUser(userId);
        List<Request> requests = List.of(
                TestUtils.makeRequest(1, LocalDateTime.now(), user),
                TestUtils.makeRequest(2, LocalDateTime.now(), user),
                TestUtils.makeRequest(3, LocalDateTime.now(), user)
        );

        when(userService.getById(userId)).thenReturn(user);
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(Collections.emptyList());
        when(requestRepository.findAllByUserIdIsNotOrderByCreatedDesc(anyLong(), any())).thenReturn(requests);

        assertThat(requestService.getOtherRequests(userId, null)).isEqualTo(requests
                .stream()
                .map(requestMapper::requestToRequestDto)
                .map(requestDto -> {
                    requestDto.setItems(Collections.emptyList());
                    return requestDto;
                }).collect(Collectors.toList()));
    }

    @Test
    void getById_shouldReturnRequest() {
        long requestId = 1;
        long userId = 1;
        User user = TestUtils.makeUser(userId);
        Request request = TestUtils.makeRequest(1, LocalDateTime.now(), user);
        RequestDto requestDto = requestMapper.requestToRequestDto(request);
        requestDto.setItems(Collections.emptyList());

        when(userService.getById(userId)).thenReturn(user);
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(Collections.emptyList());
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));

        assertThat(requestService.getById(requestId, userId)).isEqualTo(requestDto);
    }
}
