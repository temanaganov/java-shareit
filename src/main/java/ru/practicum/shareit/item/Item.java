package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@With
@Value
@Builder
public class Item {
    long id;
    String name;
    String description;
    Boolean available;
    long owner;
    long request;
}
