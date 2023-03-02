package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    Request createRequestDtoToRequest(CreateRequestDto dto);

    RequestDto requestToRequestDto(Request request);
}
