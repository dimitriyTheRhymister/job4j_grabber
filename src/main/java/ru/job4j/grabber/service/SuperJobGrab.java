package ru.job4j.grabber.service;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import ru.job4j.grabber.stores.Store;

import java.sql.SQLException;

public class SuperJobGrab implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        var store = (Store) context.getJobDetail().getJobDataMap().get("store");
        try {
            for (var post : store.getAll()) {
                System.out.println(post.getTitle());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}