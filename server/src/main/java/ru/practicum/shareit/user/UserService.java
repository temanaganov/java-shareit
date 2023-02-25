package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.core.exception.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("user", id));
    }

    public User create(CreateUserDto dto) {
        User newUser = userMapper.createUserDtoToUser(dto);

        return userRepository.save(newUser);
    }

    public User update(long id, UpdateUserDto dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("user", id));

        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            checkIfEmailExists(dto.getEmail());
            user.setEmail(dto.getEmail());
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }

        return userRepository.save(user);
    }

    public User delete(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("user", id));
        userRepository.deleteById(id);
        return user;
    }

    private void checkIfEmailExists(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new DuplicatedEmailException(email);
        });
    }
}
