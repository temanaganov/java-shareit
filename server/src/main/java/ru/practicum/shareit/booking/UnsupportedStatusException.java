package ru.practicum.shareit.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedStatusException extends RuntimeException {
    public UnsupportedStatusException() {
        super("Unknown state: UNSUPPORTED_STATUS");
    }
}
