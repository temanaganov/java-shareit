package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.CreateUserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User createUserDtoToUser(CreateUserDto dto);
}
