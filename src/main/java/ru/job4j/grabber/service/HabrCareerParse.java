package ru.job4j.grabber.service;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import ru.job4j.grabber.model.Post;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {
    private static final Logger LOGGER = Logger.getLogger(HabrCareerParse.class);
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PREFIX = "/vacancies?page=";
    private static final String SUFFIX = "&q=Java%20developer&type=all";
    private static final int PAGES_TO_PARSE = 5;

    @Override
    public List<Post> fetch() {
        var result = new ArrayList<Post>();
        try {
            for (int pageNumber = 1; pageNumber <= PAGES_TO_PARSE; pageNumber++) {
                String fullLink = "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, pageNumber, SUFFIX);
                var connection = Jsoup.connect(fullLink);
                var document = connection.get();
                var rows = document.select(".vacancy-card__inner");

                System.out.println("Processing page: " + pageNumber); // Добавьте это......................................my

                rows.forEach(row -> {
                    var titleElement = row.select(".vacancy-card__title").first();
                    if (titleElement != null) {
                        var linkElement = titleElement.child(0);
                        String vacancyName = titleElement.text();
                        String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));

                        var dateElement = row.select(".vacancy-card__date").first();
                        if (dateElement != null) {
                            var date = dateElement.child(0);
                            String dateString = date.attr("datetime");
                            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_ZONED_DATE_TIME);
                            long time = zonedDateTime.toInstant().toEpochMilli();

                            String description;
                            try {
                                description = retrieveDescription(link);
                            } catch (IOException e) {
                                LOGGER.warn("Failed to retrieve description for link: " + link, e);
                                description = "Description not available";
                            }

                            var post = new Post();
                            post.setTitle(vacancyName);
                            post.setLink(link);
                            post.setDescription(description);
                            post.setTime(time);
                            result.add(post);
                        } else {
                            LOGGER.warn("Date element not found for vacancy: " + vacancyName);
                        }
                    } else {
                        LOGGER.warn("Title element not found in row: " + row);
                    }
                });
            }
        } catch (IOException e) {
            LOGGER.error("Error loading page", e);
        }
        return result;
    }

    private String retrieveDescription(String link) throws IOException {
        var connection = Jsoup.connect(link);
        var document = connection.get();
        var rows = document.select(".vacancy-description__text");

        return rows.text();
    }

    public static void main(String[] args) {
        HabrCareerParse habrCareerParse = new HabrCareerParse();
        List<Post> list = habrCareerParse.fetch();
        System.out.println("Fetched posts: " + list.size()); // Проверка количества извлеченных вакансий
    }
}