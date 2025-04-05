package ru.job4j.grabber.stores;

import ru.job4j.grabber.model.Post;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcStore implements Store {
    private final Connection connection;

    public JdbcStore(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement pS = connection.prepareStatement(
                "INSERT INTO post(title, link, description, time) VALUES (?, ?, ?, ?)")) {
            pS.setString(1, post.getTitle());
            pS.setString(2, post.getLink());
            pS.setString(3, post.getDescription());
            pS.setLong(4, post.getTime());
            pS.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Post> getAll() throws SQLException {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement pS = connection.prepareStatement(
                "SELECT id, title, link, description, time FROM post")) {
            ResultSet rS = pS.executeQuery();
            while (rS.next()) {
                posts.add(createPostFromResultSet(rS));
            }
            System.out.println("Количество записей в БД: " + posts.size()); // Отладочный вывод
            return posts;
        }
    }

    private Post createPostFromResultSet(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String title = rs.getString("title");
        String link = rs.getString("link");
        String description = rs.getString("description");
        long time = rs.getLong("time");
        return new Post(id, title, link, description, time);
    }

    @Override
    public Optional<Post> findById(Long id) {
        try (PreparedStatement pS = connection.prepareStatement(
                "SELECT id, title, link, description, time FROM post WHERE id = ?")) {
            pS.setLong(1, id);
            ResultSet rS = pS.executeQuery();
            if (rS.next()) {
                return Optional.of(createPostFromResultSet(rS));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
}