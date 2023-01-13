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
    private final UserMapper userMapper;

    public List<User> getAll() {
        return userRepository.getAll();
    }

    public User getById(long id) {
        return userRepository.getById(id).orElseThrow(() -> new NotFoundException("user", id));
    }

    public User create(CreateUserDto dto) {
        checkIfEmailIsBusy(dto.getEmail());
        User newUser = userMapper.createUserDtoToUser(dto);

        return userRepository.create(newUser);
    }

    public User update(long id, UpdateUserDto dto) {
        User user = userRepository.getById(id).orElseThrow(() -> new NotFoundException("user", id));

        if (dto.getEmail() != null) {
            checkIfEmailIsBusy(dto.getEmail());
            user.setEmail(dto.getEmail());
        }

        if (dto.getName() != null) {
            user.setName(dto.getName());
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
