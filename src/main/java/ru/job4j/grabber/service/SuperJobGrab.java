package ru.job4j.grabber.service;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.stores.Store;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.util.List;

public class SuperJobGrab implements Job {
    private static final Logger LOGGER = Logger.getLogger(SuperJobGrab.class);
    @Override
    public void execute(JobExecutionContext context) {
        var store = (Store) context.getJobDetail().getJobDataMap().get("store");

        HabrCareerDateTimeParser habrCareerDateTimeParser = new HabrCareerDateTimeParser();
        HabrCareerParse parser = new HabrCareerParse(habrCareerDateTimeParser);
        LOGGER.info("parser создан ok");

        List<Post> posts = parser.fetch(HabrCareerParse.SOURCE_LINK); // получаем вакансии
        LOGGER.info("Fetched posts: " + posts.size());

        for (Post post : posts) {
            store.save(post); // сохраняем каждую вакансию в базе данных
            System.out.println("Saved post: " + post.getTitle()); // выводим заголовок вакансии в консоль
        }
    }
}