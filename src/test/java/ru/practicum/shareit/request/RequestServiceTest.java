package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.core.exception.NotFoundException;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.utils.TestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private RequestRepository requestRepository;

    @Spy
    private RequestMapper requestMapper = Mappers.getMapper(RequestMapper.class);

    @InjectMocks
    private RequestService requestService;

    @Test
    void createRequest_shouldCreateNewRequest() {
        long userId = 1;
        User user = TestUtils.makeUser(userId);
        CreateRequestDto createRequestDto = new CreateRequestDto("New request");

        Mockito.when(userService.getById(userId)).thenReturn(user);
        Mockito.when(requestRepository.save(Mockito.any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        RequestDto requestDto = requestService.createRequest(createRequestDto, userId);

        assertThat(requestDto.getDescription()).isEqualTo(createRequestDto.getDescription());
        assertThat(requestDto.getCreated()).isBefore(LocalDateTime.now());
        assertThat(requestDto.getItems()).isNull();
    }

    @Test
    void createRequest_shouldThrowNotFoundExceptionIfUserIsNotExists() {
        long userId = 1;
        CreateRequestDto createRequestDto = new CreateRequestDto("New request");

        Mockito.when(userService.getById(userId)).thenThrow(NotFoundException.class);

        assertThatThrownBy(() -> {
            requestService.createRequest(createRequestDto, userId);
        }).isInstanceOf(NotFoundException.class);
    }
}
