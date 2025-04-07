package ru.job4j.grabber.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class HabrCareerDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime convertStringToLocalDateTime(String parse) {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(parse);
        return zonedDateTime.toLocalDateTime();
    }

    public LocalDateTime convertMillisToLocalDateTime(long millis) {
        Instant instant = Instant.ofEpochMilli(millis);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public long convertStringToMillis(String string) {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(string, DateTimeFormatter.ISO_ZONED_DATE_TIME);
        return zonedDateTime.toInstant().toEpochMilli();
    }
}