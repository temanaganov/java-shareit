package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.CreateUserDto;

@Component
public class UserMapper {
    User createUserDtoToUser(CreateUserDto dto) {
        return User.builder().name(dto.getName()).email(dto.getEmail()).build();
    }
}
