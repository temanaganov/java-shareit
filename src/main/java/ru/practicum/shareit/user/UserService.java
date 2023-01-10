package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.core.exception.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAll() {
        return userRepository.getAll();
    }

    public User getById(long id) {
        return userRepository.getById(id).orElseThrow(() -> new NotFoundException("user", id));
    }

    public User create(CreateUserDto dto) {
        checkIfEmailIsBusy(dto.getEmail());
        User newUser = User.builder().name(dto.getName()).email(dto.getEmail()).build();

        return userRepository.create(newUser);
    }

    public User update(long id, UpdateUserDto dto) {
        User user = userRepository.getById(id).orElseThrow(() -> new NotFoundException("user", id));

        if (dto.getEmail() != null) {
            checkIfEmailIsBusy(dto.getEmail());
            user = user.withEmail(dto.getEmail());
        }

        if (dto.getName() != null) {
            user = user.withName(dto.getName());
        }

        return userRepository.update(user);
    }

    public User delete(long id) {
        return userRepository.delete(id).orElseThrow(() -> new NotFoundException("user", id));
    }

    private void checkIfEmailIsBusy(String email) {
        userRepository.getByEmail(email).ifPresent(user -> {
            throw new DuplicatedEmailException(email);
        });
    }
}
