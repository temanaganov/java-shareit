package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@With
@Value
@Builder
public class User {
    Long id;
    String name;
    String email;
}
