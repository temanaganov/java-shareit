package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.core.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestMapper requestMapper;
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;

    public RequestDto createRequest(CreateRequestDto dto, long userId) {
        User user = userService.getById(userId);

        Request request = requestMapper.createRequestDtoToRequest(dto);
        request.setUser(user);
        request.setCreated(LocalDateTime.now());
        request = requestRepository.save(request);

        return requestMapper.requestToRequestDto(request);
    }

    public List<RequestDto> getOwnRequests(long userId) {
        userService.getById(userId);

        return requestRepository.findAllByUserIdOrderByCreatedDesc(userId)
                .stream()
                .map(this::toRequestDto)
                .collect(Collectors.toList());
    }

    public List<RequestDto> getOtherRequests(long userId, Pageable pageable) {
        userService.getById(userId);

        return requestRepository.findAllByUserIdIsNotOrderByCreatedDesc(userId, pageable)
                .stream()
                .map(this::toRequestDto)
                .collect(Collectors.toList());
    }

    public RequestDto getById(long requestId, long userId) {
        userService.getById(userId);

        Request request = requestRepository
                .findById(requestId)
                .orElseThrow(() -> new NotFoundException("request", requestId));

        return toRequestDto(request);
    }

    private RequestDto toRequestDto(Request request) {
        RequestDto requestDto = requestMapper.requestToRequestDto(request);
        List<ItemDto> items = itemRepository
                .findAllByRequestId(request.getId())
                .stream()
                .map(itemMapper::itemToItemDto)
                .collect(Collectors.toList());
        requestDto.setItems(items);

        return requestDto;
    }
}
