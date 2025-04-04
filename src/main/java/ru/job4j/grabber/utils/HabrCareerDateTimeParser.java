package ru.job4j.grabber.utils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class HabrCareerDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime parse(String parse) {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(parse);
        return zonedDateTime.toLocalDateTime();
    }

    public static void main(String[] args) {
        HabrCareerDateTimeParser habrCareerDateTimeParser = new HabrCareerDateTimeParser();
        LocalDateTime localDateTime = habrCareerDateTimeParser.parse("2025-03-28T10:28:02+03:00");
        System.out.println(localDateTime);
        Class<?> cls = localDateTime.getClass();
        System.out.println(cls.getSimpleName());
    }
}