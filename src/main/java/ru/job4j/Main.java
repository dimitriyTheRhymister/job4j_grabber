package ru.job4j;

import ru.job4j.grabber.service.Config;
import ru.job4j.grabber.service.SchedulerManager;
import ru.job4j.grabber.service.SuperJobGrab;
import ru.job4j.grabber.stores.JdbcStore;
import org.apache.log4j.Logger;
import ru.job4j.grabber.stores.Store;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            Config config = new Config();
            LOGGER.info("1-конфиг создан ok");

            config.load("src/main/resources/config.properties");
            LOGGER.info("3-загрузка config.properties ok");

            Connection connection = DriverManager.getConnection(
                    config.get("db.url"),
                    config.get("db.user"),
                    config.get("db.password")
            );
            LOGGER.info("7-подключение к БД ok");

            Store store = new JdbcStore(connection);
            LOGGER.info("8-создание экземпляра JdbcStore ok");
            System.out.println(store.getAll() + "????здесь пока д б пусто???");

            SchedulerManager schedulerManager = new SchedulerManager();
            LOGGER.info("9-создание экземпляра SchedulerManager ok");
            schedulerManager.init();
            LOGGER.info("11-инит SchedulerManager ok");
            schedulerManager.load(Integer.parseInt(config.get("parser.interval")), SuperJobGrab.class, store);
            LOGGER.info("11+-получили интервал из properties это будет после 12,13,14,15? ok");
            LOGGER.info("16-загрузили интервал, работу и хранилище в SchedulerManager и значит дали старт парсингу? ok");
            System.out.println(schedulerManager + "???что здесь?просто объект");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                schedulerManager.close();
                try {
                    connection.close();
                    LOGGER.info("завершения приложения successfully");
                } catch (SQLException e) {
                    LOGGER.error("Ошибка при закрытии подключения к БД", e);
                }
            }));
        } catch (SQLException e) {
            LOGGER.error("Ошибка при работе с базой данных", e);
        } catch (Exception e) {
            LOGGER.error("Произошла ошибка", e);
        }
    }
}