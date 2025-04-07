package ru.job4j.grabber.utils;

import java.time.LocalDateTime;

public interface DateTimeParser {
    LocalDateTime convertStringToLocalDateTime(String string);

    LocalDateTime convertMillisToLocalDateTime(long millis);

    long convertStringToMillis(String string);
}