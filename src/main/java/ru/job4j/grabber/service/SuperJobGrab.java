package ru.job4j.grabber.service;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.stores.Store;

import java.util.List;

//public class SuperJobGrab implements Job {
//
//    @Override
//    public void execute(JobExecutionContext context) {
//        var store = (Store) context.getJobDetail().getJobDataMap().get("store");
//        try {
//            for (var post : store.getAll()) {
//                System.out.println(post.getTitle());
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}

public class SuperJobGrab implements Job {
    private static final Logger LOGGER = Logger.getLogger(SuperJobGrab.class);
    @Override
    public void execute(JobExecutionContext context) {
        var store = (Store) context.getJobDetail().getJobDataMap().get("store");

        HabrCareerParse parser = new HabrCareerParse();
        LOGGER.info("parser создан ok");

        List<Post> posts = parser.fetch(); // Получите вакансии
        LOGGER.info("Fetched posts: " + posts.size());

        for (Post post : posts) {
            store.save(post); // Сохраняем каждую вакансию в базе данных
            System.out.println("Saved post: " + post.getTitle()); // Выводим заголовок вакансии в консоль
        }
    }
}