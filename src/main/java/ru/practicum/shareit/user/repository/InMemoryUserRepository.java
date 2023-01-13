package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 0;

    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    public Optional<User> getById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return users.values().stream().filter(user -> user.getEmail().equals(email)).findFirst();
    }

    public User create(User user) {
        long id = getNextId();
        user.setId(id);
        users.put(id, user);

        return user;
    }

    public User update(User user) {
        users.put(user.getId(), user);

        return user;
    }

    public Optional<User> delete(long id) {
        return Optional.ofNullable(users.remove(id));
    }

    private long getNextId() {
        return ++id;
    }
}
