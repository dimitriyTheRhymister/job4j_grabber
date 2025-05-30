package ru.job4j.grabber.stores;

import ru.job4j.grabber.model.Post;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Store {
    void save(Post post);

    List<Post> getAll() throws SQLException;

    Optional<Post> findById(Long id);
}