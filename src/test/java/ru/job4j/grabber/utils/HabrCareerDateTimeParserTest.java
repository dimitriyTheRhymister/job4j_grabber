package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class HabrCareerDateTimeParserTest {

    @Test
    void whenParseOutputStringCorrect() {
        String input = "2025-03-28T10:28:02+03:00";
        HabrCareerDateTimeParser habrCareerDateTimeParser = new HabrCareerDateTimeParser();
        LocalDateTime output = habrCareerDateTimeParser.parse(input);
        String expected = output.toString();
        assertThat(output).isEqualTo(expected);
    }

    @Test
    void whenParseOutputClassCorrect() {
        String input = "2025-03-28T10:28:02+03:00";
        HabrCareerDateTimeParser habrCareerDateTimeParser = new HabrCareerDateTimeParser();
        LocalDateTime output = habrCareerDateTimeParser.parse(input);
        Class<?> cls = output.getClass();
        String expected = "LocalDateTime";
        assertThat(cls.getSimpleName()).isEqualTo(expected);
    }
}