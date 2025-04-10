package ru.job4j.grabber.service;

import io.javalin.Javalin;
import org.apache.log4j.Logger;
import ru.job4j.grabber.stores.JdbcStore;
import ru.job4j.grabber.stores.Store;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.awt.Desktop;
import java.net.URI;

public class WebApp {
    private static final Logger LOGGER = Logger.getLogger(WebApp.class);

    public static void main(String[] args) throws SQLException {
        HabrCareerDateTimeParser habrCareerDateTimeParser = new HabrCareerDateTimeParser();
        var config = new Config();
        config.load("src/main/resources/config.properties");
        String url = config.get("db.url");
        String username = config.get("db.user");
        String password = config.get("db.password");
        int interval = Integer.parseInt(config.get("parser.interval"));
        int port = Integer.parseInt(config.get("server.port"));

        Connection connection;
        LOGGER.info("1 successfully");
        System.out.println(url + username + password);

        connection = DriverManager.getConnection(url, username, password);
        Store dbStore = new JdbcStore(connection);
        LOGGER.info("2 successfully");

        SchedulerManager schedulerManager = new SchedulerManager();
        schedulerManager.init();
        schedulerManager.load(
                interval,
                SuperJobGrab.class,
                dbStore);
        LOGGER.info("3 successfully");

        Javalin app = Javalin.create().start(port);
        LOGGER.info("Сервер запущен на порту 7000");

        app.get("/", ctx -> {
            LOGGER.info("Запрос получен на /");
            ctx.result("Welcome to the Simple App!");
        });

        app.get("/test", ctx -> {
            LOGGER.info("Запрос получен на /test");
            ctx.result("Test endpoint is working");
        });

        app.get("/jobs", ctx -> {
            LOGGER.info("Запрос получен на /jobs");
            ctx.result("Jobs endpoint is working");

            var jobsFromDb = dbStore.getAll();

            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>")
                    .append("<html lang=\"ru\">")
                    .append("<head>")
                    .append("<meta charset=\"UTF-8\">")
                    .append("<title>Available Jobs</title>")
                    .append("</head>")
                    .append("<body>")
                    .append("<h1>Available Jobs:</h1>")
                    .append("<hr>")
                    .append("<ol>");

            for (var job : jobsFromDb) {
                html.append("<li>")
                        .append(job.getTitle())
                        .append(" - ")
                        .append("<a href=\"")
                        .append(job.getLink())
                        .append("\">")
                        .append(job.getLink())
                        .append("</a>")
                        .append(" - ")
                        .append(habrCareerDateTimeParser.convertMillisToLocalDateTime(job.getTime()))
                        .append("</li>")
                        .append("<br>")
                        .append(job.getDescription())
                        .append("<br>")
                        .append("<br>");
            }

            html.append("</ol>")
                    .append("</body>")
                    .append("</html>");

            ctx.html(html.toString());
        });

        String url2 = "http://localhost:7000/jobs";
        openWebPage(url2);
    }

    private static void openWebPage(String urlString) {
        try {
            URI uri = new URI(urlString);
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(uri);
        } catch (Exception e) {
            LOGGER.error("Произошла ошибка", e);
        }
    }
}