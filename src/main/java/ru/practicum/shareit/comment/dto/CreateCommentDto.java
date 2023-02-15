package ru.practicum.shareit.comment.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentDto {
    @NotBlank(message = "Text is required")
    private String text;
}
