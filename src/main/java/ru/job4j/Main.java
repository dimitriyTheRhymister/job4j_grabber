package ru.job4j;

import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.service.Config;
import ru.job4j.grabber.service.SchedulerManager;
import ru.job4j.grabber.service.SuperJobGrab;
import ru.job4j.grabber.stores.JdbcStore;
import org.apache.log4j.Logger;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        final Logger log = Logger.getLogger(SchedulerManager.class);
        var config = new Config();
        config.load("application.properties");
        try (var connection = DriverManager.getConnection("")) {
            var store = new JdbcStore(connection);
            var post = new Post(1L, "2", "3", "4", 5L);
            post.setTitle("Super Java Job");
            store.save(post);
            var scheduler = new SchedulerManager();
            scheduler.init();
            scheduler.load(
                    Integer.parseInt(config.get("rabbit.interval")),
                    SuperJobGrab.class,
                    store);
        } catch (SQLException e) {
            log.error("When create a connection", e);
        }
    }
}