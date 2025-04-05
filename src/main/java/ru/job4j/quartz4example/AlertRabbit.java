package ru.job4j.quartz4example;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;

import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            String param1 = context.getJobDetail().getJobDataMap().getString("param1");
            int param2 = context.getJobDetail().getJobDataMap().getInt("param2");

            System.out.println("Rabbit runs here with param1: " + param1 + " and param2: " + param2);
        }
    }

    private static Properties getConfig() throws Exception {
        Properties config = new Properties();
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            if (in == null) {
                throw new FileNotFoundException("Property file 'rabbit.properties' not found in the classpath");
            }
            config.load(in);
        }
        return config;
    }

    public static void main(String[] args) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();

            JobDetail job = newJob(Rabbit.class)
                    .usingJobData("param1", "Hello, Rabbit!")
                    .usingJobData("param2", 42)
                    .build();

            Properties config = getConfig();
            String str = config.getProperty("rabbit.interval");
            int interval = Integer.parseInt(str);

            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval)
                    .repeatForever();

            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();

            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException se) {
            se.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}