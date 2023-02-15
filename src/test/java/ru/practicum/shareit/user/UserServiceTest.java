package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.core.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.utils.TestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getById_shouldThrowNotFoundException() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getById(1)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getById_shouldReturnUser() {
        long id = 1;
        User user = TestUtils.makeUser(id);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        assertThat(userService.getById(id)).isEqualTo(user);
    }

    @Test
    void update_shouldThrowNotFoundException() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getById(1)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_shouldUpdateEmail() {
        long id = 1;
        String newEmail = "newEmail@test.test";

        UpdateUserDto dto = new UpdateUserDto(null, newEmail);
        User user = TestUtils.makeUser(id);
        User newUser = TestUtils.makeUser(id);
        newUser.setEmail(newEmail);

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        assertThat(userService.update(id, dto)).isEqualTo(newUser);
    }

    @Test
    void update_shouldThrowDuplicateEmailException() {
        long id = 1;
        String newEmail = "newEmail@test.test";

        UpdateUserDto dto = new UpdateUserDto(null, newEmail);
        User user = TestUtils.makeUser(id);

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findByEmail(newEmail)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.update(id, dto)).isInstanceOf(DuplicatedEmailException.class);
    }

    @Test
    void update_shouldUpdateName() {
        long id = 1;
        String newName = "new name";

        UpdateUserDto dto = new UpdateUserDto(newName, null);
        User user = TestUtils.makeUser(id);
        User newUser = TestUtils.makeUser(id);
        newUser.setName(newName);

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        assertThat(userService.update(id, dto)).isEqualTo(newUser);
    }

    @Test
    void getAll_shouldReturnListOfUsers() {
        List<User> users = List.of(
                TestUtils.makeUser(1),
                TestUtils.makeUser(2),
                TestUtils.makeUser(3)
        );

        Mockito.when(userRepository.findAll()).thenReturn(users);

        assertThat(userService.getAll()).isEqualTo(users);
    }

    @Test
    void delete_shouldReturnDeletedUser() {
        long userId = 1;
        User user = TestUtils.makeUser(userId);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThat(userService.delete(userId)).isEqualTo(user);
    }
}
