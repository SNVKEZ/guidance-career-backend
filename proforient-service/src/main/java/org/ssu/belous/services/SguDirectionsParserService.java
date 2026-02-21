package org.ssu.belous.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.ssu.belous.models.Direction;
import org.ssu.belous.models.Faculty;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SguDirectionsParserService {

    private static final String DIRECTIONS_URL = "https://www.sgu.ru/postuplenie-v-sgu/bakalavriat-i-specialitet-perechen";
    private static final String STRUCTURE_URL = "https://www.sgu.ru/struktura";
    private static final String BASE_URL = "https://www.sgu.ru";

    private static final Pattern DASH_SURROUNDED_BY_LETTERS = Pattern.compile("[а-яА-Я]\\s*[-–—]\\s*[а-яА-Я]");

    public List<Faculty> parseFacultiesWithDirectionsAndLinks() {
        try {
            Map<String, List<String>> facultyToDirections = parseBachelorSpecialistDirectionsFromList();
            Map<String, String> nameToLink = parseMainFacultyLinksFromStructure();

            List<Faculty> faculties = new ArrayList<>();

            for (Map.Entry<String, List<String>> entry : facultyToDirections.entrySet()) {
                String facultyName = entry.getKey().trim();
                Faculty faculty = new Faculty();
                faculty.setName(facultyName);

                String href = nameToLink.get(facultyName);
                faculty.setLink(href != null ? BASE_URL + href : null);

                List<Direction> directions = entry.getValue().stream()
                        .map(dirName -> {
                            Direction dir = new Direction();
                            dir.setName(dirName);
                            dir.setFaculty(faculty);
                            return dir;
                        })
                        .toList();

                faculty.setDirections(directions);
                faculties.add(faculty);
            }

            faculties.sort(Comparator.comparing(Faculty::getName, String.CASE_INSENSITIVE_ORDER));

            return faculties;

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при парсинге страниц СГУ: " + e.getMessage(), e);
        }
    }

    private Map<String, List<String>> parseBachelorSpecialistDirectionsFromList() throws IOException {
        Document doc = Jsoup.connect(DIRECTIONS_URL)
                .userAgent("Mozilla/5.0 (compatible; ParserBot/1.0)")
                .timeout(15000)
                .get();

        Map<String, List<String>> result = new LinkedHashMap<>();

        Elements accordions = doc.select("div.accordion-container");

        for (Element accordion : accordions) {
            Element header = accordion.selectFirst("h2.accordion__header");
            if (header == null) continue;

            String facultyName = header.text().trim();
            if (facultyName.isEmpty()) continue;

            Element table = accordion.selectFirst("table");
            if (table == null) continue;

            List<String> directions = extractDirectionsFromTable(table);

            if (!directions.isEmpty()) {
                result.put(facultyName, directions);
            }
        }

        return result;
    }

    private List<String> extractDirectionsFromTable(Element table) {
        List<String> directions = new ArrayList<>();
        Elements rows = table.select("tr");
        for (Element row : rows) {
            Elements cells = row.select("td");
            if (cells.isEmpty()) continue;
            String text = cells.first().text().trim();
            if (text.matches(".*\\d{2}\\.\\d{2}\\.\\d{2,3}.*")) {
                String cleaned;
                Matcher matcher = DASH_SURROUNDED_BY_LETTERS.matcher(text);
                if (matcher.find()) {
                    cleaned = text;
                } else {
                    cleaned = text.replaceAll("[-–—].*$", "").trim();
                }
                cleaned = cleaned.replaceAll("\\s+", " ").trim();
                if (!cleaned.isEmpty() && !directions.contains(cleaned)) {
                    directions.add(cleaned);
                }
            }
        }

        return directions.stream()
                .sorted(Comparator.comparing(this::extractCodeFromDirection))
                .toList();
    }

    private String extractCodeFromDirection(String directionName) {
        Matcher m = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{2,3})").matcher(directionName);
        return m.find() ? m.group(1) : "999.99.99";
    }

    private Map<String, String> parseMainFacultyLinksFromStructure() throws IOException {
        Document doc = Jsoup.connect(STRUCTURE_URL)
                .userAgent("Mozilla/5.0 (compatible; ParserBot/1.0)")
                .timeout(15000)
                .get();

        Map<String, String> nameToLink = new HashMap<>();

        Elements facultyLinks = doc.select("h4.struct__sub-title > a.struct__sub-link");

        for (Element link : facultyLinks) {
            String name = link.text().trim();
            String href = link.attr("href").trim();

            if (!name.isEmpty() && href.startsWith("/struktura/")) {
                nameToLink.put(name, href);
            }
        }

        return nameToLink;
    }
}