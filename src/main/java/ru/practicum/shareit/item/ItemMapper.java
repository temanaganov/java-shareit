package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.CreateItemDto;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item createItemDtoToItem(CreateItemDto dto);
}
