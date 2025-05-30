package ru.job4j.grabber.service;

import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.job4j.grabber.stores.Store;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class SchedulerManager {
    private static final Logger LOGGER = Logger.getLogger(SchedulerManager.class);
    private Scheduler scheduler;

    public void init() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            LOGGER.info("10-scheduler стартовал ok");

        } catch (SchedulerException se) {
            LOGGER.error("When init scheduler", se);
        }
    }

    public void load(int period, Class<SuperJobGrab> task, Store store) {
        try {
            var data = new JobDataMap();
            data.put("store", store);
            var job = newJob(task)
                    .usingJobData(data)
                    .build();
            LOGGER.info("12-newJob построили ok");

            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(period)
                    .repeatForever();
            LOGGER.info("13-simpleSchedule создали ok");

            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            LOGGER.info("14-trigger создали ok");

            scheduler.scheduleJob(job, trigger);
            LOGGER.info("15-в scheduleJob дали job и trigger=start работе ok");
         } catch (SchedulerException se) {
            LOGGER.error("When init job", se);
        }
    }

    public void close() {
        if (scheduler != null) {
            try {
                scheduler.shutdown();
            } catch (SchedulerException e) {
                LOGGER.error("When shutdown scheduler", e);
            }
        }
    }
}