package utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DateTimeUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter FORMATTER_HH = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .withZone(ZoneOffset.UTC);

    public static String getCurrentUtcTimestamp() {
        return FORMATTER_HH.format(Instant.now());
    }

    public static String getCurrentTimeInYYYYMMddFormat() {
        return FORMATTER.format(Instant.now());
    }

    public static String addDays(String dateString, int days) {
        try {
            // Normalize dateString to YYYY-MM-DD if it contains time information
            if (dateString.contains("T")) {
                dateString = dateString.substring(0, 10); // Extract YYYY-MM-DD part
            }

            LocalDate date = LocalDate.parse(dateString, FORMATTER);
            LocalDate nextDay = date.plusDays(days);
            return nextDay.format(FORMATTER);
        } catch (DateTimeParseException e) {
            return "Invalid date format. Please use YYYY-MM-DD.";
        }
    }



    public static String subtractDays(String dateString, int days) {
        try {
            LocalDate date = LocalDate.parse(dateString, FORMATTER);
            LocalDate nextDay = date.minusDays(days);
            return nextDay.format(FORMATTER);
        } catch (DateTimeParseException e) {
            return "Invalid date format. Please use YYYY-MM-DD.";
        }
    }

    public static String formatDate(List<Integer> dateList) {
        if (dateList != null && dateList.size() == 3) {
            return String.format("%04d-%02d-%02d", dateList.get(0), dateList.get(1), dateList.get(2));
        }
        return "Invalid or missing endDate format.";
    }
}
