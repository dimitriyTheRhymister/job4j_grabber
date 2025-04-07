package ru.job4j.grabber.service;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HabrCareerParse implements Parse {
    private static final Logger LOGGER = Logger.getLogger(HabrCareerParse.class);
    static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PREFIX = "/vacancies?page=";
    private static final String SUFFIX = "&q=Java%20developer&type=all";
    private static final int PAGES_TO_PARSE = 5;
    private final HabrCareerDateTimeParser habrCareerDateTimeParser = new HabrCareerDateTimeParser();

    @Override
    public List<Post> fetch() {
        List<Post> result = new ArrayList<>();
        try {
            for (int pageNumber = 1; pageNumber <= PAGES_TO_PARSE; pageNumber++) {
                String fullLink = String.format("%s%s%d%s", SOURCE_LINK, PREFIX, pageNumber, SUFFIX);
                processPage(fullLink, result);
            }
        } catch (IOException e) {
            LOGGER.error("Error loading pages", e);
        }
        return result;
    }

    private void processPage(String fullLink, List<Post> result) throws IOException {
        var document = Jsoup.connect(fullLink).get();
        var rows = document.select(".vacancy-card__inner");

        System.out.println("Processing page: " + fullLink);

        rows.forEach(row -> {
            Optional<Post> post = createPostFromRow(row);
            post.ifPresent(result::add);
        });
    }

    private Optional<Post> createPostFromRow(Element row) {
        var titleElement = row.select(".vacancy-card__title").first();
        if (titleElement == null) {
            LOGGER.warn("Title element not found in row: " + row);
            return Optional.empty();
        }

        var linkElement = titleElement.child(0);
        String vacancyName = titleElement.text();
        String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
        var dateElement = row.select(".vacancy-card__date").first();

        if (dateElement == null) {
            LOGGER.warn("Date element not found for vacancy: " + vacancyName);
            return Optional.empty();
        }

        var date = dateElement.child(0);
        String dateString = date.attr("datetime");
        long time = habrCareerDateTimeParser.convertStringToMillis(dateString);

        String description = retrieveDescription(link)
                .orElse("Description not available");

        Post post = new Post();
        post.setTitle(vacancyName);
        post.setLink(link);
        post.setDescription(description);
        post.setTime(time);

        return Optional.of(post);
    }

    private Optional<String> retrieveDescription(String link) {
        try {
            var connection = Jsoup.connect(link);
            var document = connection.get();
            var rows = document.select(".vacancy-description__text");
            return Optional.of(rows.text());
        } catch (IOException e) {
            LOGGER.warn("Failed to retrieve description for link: " + link, e);
            return Optional.empty();
        }
    }
}